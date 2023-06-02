################################################################
# Minimal SBT-JNI Meson C/C++ Project                          #
#                                                              #
# All settings required by sbt-jni have been marked so, please #
# add/modify/remove settings to build your specific library.   #
################################################################

# Define project and languages used
# (required by sbt-jni) please use semantic versioning
# Note: Java is required in the list of languages to link JNI
#
project('{{project}}', 'c', 'cpp', 'java', version: '0.1.0')

# Find JNI headers
java_dep = dependency('jni', include_type: 'system')

# Setup project name
# (required by sbt-jni) major version should always be appended to library name
#
major_version = meson.project_version().split('.')[0]
lib_name = '@0@@1@'.format(meson.project_name(), major_version)

# Setup installation targets
# (required by sbt-jni) major version should always be appended to library name
#
shared_library(lib_name,
    sources: [
        'library.c',
    ],
    install : true,
    include_directories : include_directories(
        '.',
        'include',
    ),
    dependencies : [
        java_dep,
    ])
