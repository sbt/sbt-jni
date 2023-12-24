# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.7.0] - 2023-12-24
## Added

- Add support of multiple native outputs [#186](https://github.com/sbt/sbt-jni/pull/186) (@pomadchin)
- Add support for Meson build tool [#147](https://github.com/sbt/sbt-jni/pull/147) (@mihnea-s)

## Changed

- Bump actions/setup-java from 3 to 4 [#187](https://github.com/sbt/sbt-jni/pull/187) (@dependabot)
- Update sbt, scripted-plugin to 1.9.7 [#181](https://github.com/sbt/sbt-jni/pull/181) (@scala-steward)
- Update asm to 9.6 [#179](https://github.com/sbt/sbt-jni/pull/179) (@scala-steward)
- Update sbt, scripted-plugin to 1.9.6 [#172](https://github.com/sbt/sbt-jni/pull/172) (@scala-steward)
- Update scala3-compiler, scala3-library to 3.3.1 [#168](https://github.com/sbt/sbt-jni/pull/168) (@scala-steward)
- Update scalatest to 3.2.17 [#171](https://github.com/sbt/sbt-jni/pull/171) (@scala-steward)
- Update scalafmt-core to 3.7.14 [#170](https://github.com/sbt/sbt-jni/pull/170) (@scala-steward)
- Update sbt-scalafmt to 2.5.2 [#169](https://github.com/sbt/sbt-jni/pull/169) (@scala-steward)
- Update scala-compiler, scala-library, ... to 2.13.12 [#167](https://github.com/sbt/sbt-jni/pull/167) (@scala-steward)
- Update sbt, scripted-plugin to 1.9.4 [#166](https://github.com/sbt/sbt-jni/pull/166) (@scala-steward)
- Update sbt, scripted-plugin to 1.9.3 [#163](https://github.com/sbt/sbt-jni/pull/163) (@scala-steward)
- Update sbt, scripted-plugin to 1.9.2 [#159](https://github.com/sbt/sbt-jni/pull/159) (@scala-steward)
- Update scala-compiler, scala-library, ... to 2.13.11 [#154](https://github.com/sbt/sbt-jni/pull/154) (@scala-steward)
- Update scala-compiler, scala-library, ... to 2.12.18 [#153](https://github.com/sbt/sbt-jni/pull/153) (@scala-steward)
- Update sbt, scripted-plugin to 1.9.0 [#152](https://github.com/sbt/sbt-jni/pull/152) (@scala-steward)
- Update scala3-compiler, scala3-library to 3.3.0 [#150](https://github.com/sbt/sbt-jni/pull/150) (@scala-steward)
- Update sbt, scripted-plugin to 1.8.3 [#148](https://github.com/sbt/sbt-jni/pull/148) (@scala-steward)
- Update scalatest to 3.2.16 [#149](https://github.com/sbt/sbt-jni/pull/149) (@scala-steward)
- Update asm to 9.5 [#143](https://github.com/sbt/sbt-jni/pull/143) (@scala-steward)

## [1.6.0] - 2023-02-25
## Added

- Expose BuildTools parameters [#141](https://github.com/sbt/sbt-jni/pull/141) (@pomadchin)

## Changed

- Update scala3-compiler, scala3-library to 3.2.2 [#138](https://github.com/sbt/sbt-jni/pull/138) (@scala-steward)
- Update sbt, scripted-plugin to 1.8.2 [#135](https://github.com/sbt/sbt-jni/pull/135) (@scala-steward)
- Update scala3-compiler, scala3-library to 3.2.1 [#131](https://github.com/sbt/sbt-jni/pull/131) (@scala-steward)
- Update sbt, scripted-plugin to 1.7.3 [#129](https://github.com/sbt/sbt-jni/pull/129) (@scala-steward)
- Update scala3-compiler, scala3-library to 3.2.0 [#121](https://github.com/sbt/sbt-jni/pull/121) (@scala-steward)
- Update sbt, scripted-plugin to 1.7.2 [#126](https://github.com/sbt/sbt-jni/pull/126) (@scala-steward)
- Update asm to 9.4 [#124](https://github.com/sbt/sbt-jni/pull/124) (@scala-steward)
- Update scala-compiler, scala-library, ... to 2.12.17 [#122](https://github.com/sbt/sbt-jni/pull/122) (@scala-steward)

## Fixed

- Fix names of overloaded methods with ref type parameters [#139](https://github.com/sbt/sbt-jni/pull/139) (@berkeleybarry)


## [1.5.4] - 2022-07-24
## Added

- Enable building on Scala 2.11 [#119](https://github.com/sbt/sbt-jni/pull/119) (@michaelmior)

## Changed

- Update scala3-compiler, scala3-library to 3.1.3 [#115](https://github.com/sbt/sbt-jni/pull/115) (@scala-steward)
- Update scala-compiler, scala-library, ... to 2.12.16 [#114](https://github.com/sbt/sbt-jni/pull/114) (@scala-steward)
- Update asm to 9.3 [#105](https://github.com/sbt/sbt-jni/pull/105) (@scala-steward)
- Update scala-compiler, scala-library, ... to 2.13.8 [#94](https://github.com/sbt/sbt-jni/pull/94) (@scala-steward)
- Bump Scala and asm versions up [#62](https://github.com/sbt/sbt-jni/pull/62) (@pomadchin)

## Fixed

- Fix sbt test on java 11 [#112](https://github.com/sbt/sbt-jni/pull/112) (@MasseGuillaume)


## [1.5.3] - 2021-08-01

### Added

- Add unmanagedPlatformDependentNativeDirectories [#60](https://github.com/sbt/sbt-jni/pull/60) (@pomadchin)

## [1.5.2] - 2021-07-28

### Fixed

- Fix warning (not only) in users' code [#59](https://github.com/sbt/sbt-jni/pull/59) (@sideeffffect)

## [1.5.1] - 2021-07-27

### Fixed

- Revisit the name mangling rule usage for the '$' character  [#58](https://github.com/sbt/sbt-jni/pull/58) (@pomadchin)

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

[Unreleased]: https://github.com/sbt/sbt-jni/compare/v1.7.0...HEAD
[1.7.0]: https://github.com/sbt/sbt-jni/compare/v1.6.0...v1.7.0
[1.6.0]: https://github.com/sbt/sbt-jni/compare/v1.5.4...v1.6.0
[1.5.4]: https://github.com/sbt/sbt-jni/compare/v1.5.3...v1.5.4
[1.5.3]: https://github.com/sbt/sbt-jni/compare/v1.5.2...v1.5.3
[1.5.2]: https://github.com/sbt/sbt-jni/compare/v1.5.1...v1.5.2
[1.5.1]: https://github.com/sbt/sbt-jni/compare/v1.5.0...v1.5.1
[1.5.0]: https://github.com/sbt/sbt-jni/compare/v1.4.1...v1.5.0
[1.4.1]: https://github.com/sbt/sbt-jni/compare/v1.4.0...v1.4.1
