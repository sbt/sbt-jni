# Definitions
- Native binaries: artifacts such as libraries and executables that are platform dependent, typically C/C++ projects

# Goals:

## Wrapper
- Native binaries are compiled with another build tool, such as Make, Autotools or CMake
- Provides a "Native" configuration with most standard sbt tasks.
Why not provide separate tasks, like nativeCompile and nativeTest? individual tasks don't map, that's the whole idea to use a wrapper

## JNI
- Generate JNI headers


