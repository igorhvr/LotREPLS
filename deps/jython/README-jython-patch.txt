Summary of changes:
- add try {} catch (SecurityException e) {} blocks around library calls that
  can throw SecurityExceptions
- use a URLClassLoader instead of a SecureClassLoader
- remove Compiler.compileClass() call (it's a no-op)

jython-r5996-patched-for-appengine.jar contains a build of jython at r5996 with the patches applied.
Simply place this in your WEB-INF/lib directory to use it.

jython.patch is a patch against jython trunk at r5996.  To apply, check out the
jython trunk@r5996 and run 'patch -p0 < jython.patch'.

The patch is currently (as of 4/9/2009) in the Jython issue tracker as 1188.  It should land soon :)
