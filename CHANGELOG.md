# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- Added queue processing to handle `text-created` and `text-patched` dataset events.
- Added `POST /operations/process-text/texts/:uuid` to process a single text.
- Added `POST /process-text/questions/{uuid}` to process a question.
- Added `POST /process-text/student-answers/{uuid}` to process a student-answer.
- Added `POST /process-text/questions/{uuid}/student-answers` to process all the student-answers of a question.
- Dockerized project.

### Changed

### Removed

### Fixed

### Security

[Unreleased]: https://github.com/jlarteaga/thesis-coordinator

