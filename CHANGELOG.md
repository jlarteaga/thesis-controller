# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

### Changed

### Removed

### Fixed

### Security

## [1.0.2]

### Added

### Changed

### Removed

### Fixed

- Fixed dataset's user/password settings not being retrieved by the configuration service.

### Security

## [1.0.1]

### Added

- Added configuration settings to set user/password credentials for the dataset.

## [1.0.0]

### Added

- Added `POST /operations/similarity-matrices/texts/{uuid}` to process matrix-similarity of a given text.
- Added handler to trigger a matrix-similarity analysis when a text has been processed.
- Added handler for `student-answer-patched` events.

## [0.1.0]

### Added

- Added queue processing to handle `text-created` and `text-patched` dataset events.
- Added `POST /operations/process-text/texts/:uuid` to process a single text.
- Added `POST /process-text/questions/{uuid}` to process a question.
- Added `POST /process-text/student-answers/{uuid}` to process a student-answer.
- Added `POST /process-text/questions/{uuid}/student-answers` to process all the student-answers of a question.
- Dockerized project.

[Unreleased]: https://github.com/jlarteaga/thesis-coordinator/compare/1.0.2...develop

[1.0.2]: https://github.com/jlarteaga/thesis-coordinator/compare/1.0.1...1.0.2

[1.0.1]: https://github.com/jlarteaga/thesis-coordinator/compare/1.0.0...1.0.1

[1.0.0]: https://github.com/jlarteaga/thesis-coordinator/compare/0.1.0...1.0.0

[0.1.0]: https://github.com/jlarteaga/thesis-coordinator/releases/tag/0.1.0

