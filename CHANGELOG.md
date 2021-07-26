# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.5.0] - 2021-07-26

### Added

- Add support for Cargo (Rust) [#42](https://github.com/sbt/sbt-jni/pull/42) (@sideeffffect)
- Add CMake support of versions < 3.15 [#51](https://github.com/sbt/sbt-jni/pull/51) (@pomadchin)
- Scala 3 API & project cross compilation [#48](https://github.com/sbt/sbt-jni/pull/48) (@pomadchin)
- Use cmake platform build tool [#40](https://github.com/sbt/sbt-jni/pull/40) (@kammoh)

### Changed

- ch.jodersky => com.github.sbt packages rename [#55](https://github.com/sbt/sbt-jni/pull/55) (@pomadchin)
- Change the way sbt-jni-core scope is defined [#53](https://github.com/sbt/sbt-jni/pull/53) (@pomadchin)
- Rename macro project to core and simplify Scala 3 support [#52](https://github.com/sbt/sbt-jni/pull/52) (@pomadchin)
- Upgrade gjavah [#43](https://github.com/sbt/sbt-jni/pull/43) (@sideeffffect)
- Add scalafmt, upd SBT version, add CHANGELOG, adjust CI [#47](https://github.com/sbt/sbt-jni/pull/47) (@pomadchin)

### Fixed

- fix for #38: sbt 1.4.x [#39](https://github.com/sbt/sbt-jni/pull/39) (@kammoh)

## [1.4.1] - 2019-12-13

[Unreleased]: https://github.com/sbt/sbt-jni/compare/v1.5.0...HEAD
[1.5.0]: https://github.com/sbt/sbt-jni/compare/v1.4.1...v1.5.0
[1.4.1]: https://github.com/sbt/sbt-jni/compare/v1.4.0...v1.4.1
