#!/usr/bin/python
# -*- coding: latin-1 -*-
# Copyright 2016 Telefonica Investigacion y Desarrollo, S.A.U
#
# This file is part of Orion Context Broker.
#
# Orion Context Broker is free software: you can redistribute it and/or
# modify it under the terms of the GNU Affero General Public License as
# published by the Free Software Foundation, either version 3 of the
# License, or (at your option) any later version.
#
# Orion Context Broker is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero
# General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with Orion Context Broker. If not, see http://www.gnu.org/licenses/.
#
# For those usages not covered by this license please contact with
# iot_support at tid dot es


from getopt import getopt, GetoptError
from requests import get

import os
import sys
import re
import tempfile
import configparser

__author__ = 'fermin'


#####################
# Functions program #
#####################

def msg(m):
    """
    Print message if verbose mode is enabled

    :param m: message to print
    """
    global verbose

    if verbose:
        print m


def usage_and_exit(m):
    """
    Print usage message and exit

    :param m: optional error message to print
    """

    if m != '':
        print m
        print

    usage()
    sys.exit(1)


def usage():
    """
    Print usage message
    """

    print 'Modifies .md documentation replacing generic links pointing to master to the ones pointing to the'
    print 'right tag per component.'
    print ''
    print 'Usage: %s -d <directory> -c <conf_file> [-v] [-u]' % os.path.basename(__file__)
    print ''
    print 'Parameters:'
    print '  -d <directory>: directory to process, all .md files in it (including subdirectories in recursive way) are'
    print '     processed'
    print '  -c <conf_file>: file with the per-component version corresponding to the IoTP release'
    print '  -r: dry-run mode, i.e. files are not actually modified'
    print '  -v: verbose mode'
    print '  -u: print this usage message'


def get_repo(link):
    """
    Get repo name from a link URL. We deal with two different patterns (github.com or readthedocs.io), e.g:

      https://github.com/telefonicaid/perseo-fe/blob/master/documentation/plain_rules.md#location-fields
      http://fiware-cygnus.readthedocs.io/en/master/cygnus-ngsi/flume_extensions_catalogue/ngsi_cartodb_sink/index.html#section2.3.6

    :param link: the URL from which the repo is got
    :return: repo name
    """

    if link.startswith('https://github.com') or link.startswith('http://github.com'):
        m = re.match('https?://github.com/telefonicaid/(.*)/', link)
        # Sometimes the regex is too "greedy" and captures thinks like "orchestrator/tree/master/src/orchestrator/core"
        # so we take only the first token. Probably this could be solved at regex plane, using an altertive to
        # (.*) but it would be more complex
        return m.group(1).split('/')[0]
    else: # readthedocs pattern
        m = re.match('https?://(.*)\.readthedocs', link)
        return m.group(1)


def link_to_version(link, versions):
    """
    Returns the link to the specific component version, based on the origina link (pointing to master)
    and the repo-to-version map.

    :param link: original link (using master)
    :param versions: repo-to-version map
    :retun: specific link for the component version
    """
    return link.replace('master', versions[get_repo(link)])


def process_dir(dir_name, versions, dry_run):
    """
    Recursive dir processing.

    :param dir_name: directory to be processed
    :param versions: repo-to-version map
    :param dry_run: dry-run mode
    """

    # To be sure directory hasn't a trailing slash
    dir_name_clean = dir_name.rstrip('/')

    try:
        files = os.listdir(dir_name_clean)
    except Exception as err:
        print ('* error processing directory %s: %s' % (dir_name_clean, str(err)))
        return accum

    for file in files:

        file_name = dir_name_clean + '/' + file

        if os.path.isdir(file_name):
            process_dir(file_name, versions, dry_run)
        else:
            extension = os.path.splitext(os.path.basename(file_name))[1]
            if extension == '.md':
                process_file(file_name, versions, dry_run)


def process_file(file_name, versions, dry_run):
    """
    Process the file passed as argument, The process is based on re-writing the
    file line by line, changing the ones with links

    :param file_name: file to be processed
    :param versions: repo-to-version map
    :param dry_run: dry-run mode
    """

    msg('* processing file %s' % file_name)

    try:
        file_temp = tempfile.NamedTemporaryFile(delete=False)
        for line in open(file_name):
            m = re.match('.*(https?://\S*master\S*)', line)
            if m is not None:
                link = m.group(1)
                msg('    - link detected: %s' % link)
                new_link = link_to_version(link, versions)
                msg('    - new link:      %s' % new_link)
                if dry_run:
                    file_temp.write(line)
                else:
                    file_temp.write(line.replace(link, new_link))
            else:
                file_temp.write(line)

        file_temp.close()

        # Remove old file, replacing by the edited one
        os.remove(file_name)
        os.rename(file_temp.name, file_name)
    except Exception as err:
        print ('* error processing file %s: %s' % (file_name, str(err)))


def get_iotalib_version(repo, version):
    """
    :param repo: repository name (supposed to be an IOTA repository)
    :param version: IOTA version on that repository
    :return the iotagent-node-lib version used by that IOTA agent version, looking to package.json
    """

    url = 'https://raw.githubusercontent.com/telefonicaid/%s/%s/package.json' % (repo, version)
    r = get(url)
    return r.json()['dependencies']['iotagent-node-lib']


################
# Main program #
################


try:
    opts, args = getopt(sys.argv[1:], 'd:c:vur', [])
except GetoptError:
    usage_and_exit('wrong parameter')

# Defaults
dir = ''
conf = ''
verbose = False
dry_run = False

for opt, arg in opts:
    if opt == '-u':
        usage()
        sys.exit(0)
    elif opt == '-v':
        verbose = True
    elif opt == '-r':
        dry_run = True
    elif opt == '-d':
        dir = arg
    elif opt == '-c':
        conf = arg
    else:
        usage_and_exit('')

if dir == '':
    usage_and_exit('missing -d parameter')

if conf == '':
    usage_and_exit('missing -c parameter')

# Configuration file provides "base versions", e.g. the one which third number is 0, and that is what we use for
# documentation. Note that although the final version of the component deployed could be different (e.g. 1.4.5
# instead of 1.4.0) we can assume that minor version only contain bugfixes and that documentation is the same
versions = {}
try:
    config = configparser.ConfigParser()
    config.read(conf)
    for repo in config['branches'].keys():
        # configuration elements follow the 'release/1.4.0' patter, so .split('/')[1] takes the '1.4.0' parts
        versions[repo] = config['branches'][repo].split('/')[1]
        msg('* version for %s is %s' % (repo, versions[repo]))
except:
    print ('* %s is not a configuration file or it doesn not exist' % conf)
    sys.exit(1)

# iotagent-node-lib is special, as it is not part of IoTP releases it cannot be forund directly in
# the conf file. Thus, we need to get the packages.json corresponding to the iota and get the information
# there
iotalib_version_ul = get_iotalib_version('iotagent-ul', versions['iotagent-ul'])
iotalib_version_json = get_iotalib_version('iotagent-json', versions['iotagent-json'])

if iotalib_version_ul != iotalib_version_json:
    print '* iotagent-node-lib version used by iotagent-ul (%s) and iotagent-json (%s) do not match. Exiting.' \
          % (iot_lib_version_ul, iotalib_version_json)
    exit(1)

# .replace() needed as in some cases library version appears as '2.4.x' in packages.json
versions['iotagent-node-lib'] = iotalib_version_json.replace('x', '0')
msg('* version for iotagent-node-lib is %s' % iotalib_version_json)

if os.path.isdir(dir):
    result = process_dir(dir, versions, dry_run)
else:
    print '* %s is not a directory' % dir
