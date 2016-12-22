# fiware-iot-stack
Repository to store and manage end user documentation related to the IoT Stack FIWARE based 

## How to generate documentation for a specific release

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
