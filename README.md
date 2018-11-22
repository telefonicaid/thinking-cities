# fiware-iot-stack
Repository to store and manage end user documentation related to the IoT Stack FIWARE based 

## How to generate documentation for a specific release

Requirements:

* Python 2.7 (it may work with other versions but I haven't tested)
* Required Python modules: requests and configparser, that can ben installed typically using:

```
sudo pip install requests configparser
```

Assuming you are in master branch with everything ready to release (v4.2 is a fictional example):

* Create the release branch:

```
git checkout -b release/v4.2
```

* Adjust URLs to point to the right version of the components included in that release. This is
  done automatically by the `prepare_release.py` using as input the repo-to-version configuration
  file

```
./prepare_release.py -v -d docs/ -c iotpv4.2.conf
```

* Check that the script has done its job correctly

```
git diff
```

* Commit all changes

```
git add docs/*.md
git add docs/topics/*.md
git status   # check all the files are staged for commit
git commit
```

* Push to origin

```
git push origin release/v4.2
```

* Check at http://fiware-iot-stack.readthedocs.io/en/master/ that the new release is there (otherwise review the
  RTD configuration)

## Configuration file syntax

The configuration file (`iotpv4.2.conf` in the example above) uses the following syntax:

```
[branches]
repo-name1=release/1.1.0
repo-name2=release/1.2.0
repo-name3=release/1.3.0
...
repo-nameN=release/1.4.0
```

The 1.1.0, 1.2.0, etc. above are just example; use actual version number in a real case. 
