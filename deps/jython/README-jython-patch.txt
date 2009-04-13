Summary of changes:
- add try {} catch (SecurityException e) {} blocks around library calls that
  can throw SecurityExceptions
- use a URLClassLoader instead of a SecureClassLoader
- remove Compiler.compileClass() call (it's a no-op)

jython-r6218-patched-src.tgz contains the source of jython svn trunk@r6218 with a patch applied to work in AppEngine.

jython.patch is a patch against jython trunk at r6218.  To apply, check out the
jython trunk@r6218 and run 'patch -p0 < jython.patch'.

The patch is currently (as of 4/9/2009) in the Jython issue tracker as 1188.  It should land soon :)
