### Fast Resumable Downloader

Built using scala and akka-streams. It has following features.
1. Download large files
2. Download progress notifications
3. Resume failed downloads
4. Supports both sequential and parallel downloads

### Code Status
[![CircleCI](https://circleci.com/gh/sharmapankaj2512/resumable-downloader-scala-akka.svg?style=svg)](https://circleci.com/gh/sharmapankaj2512/resumable-downloader-scala-akka)

### Contributing

#### Setup
1. [Install scala](https://www.scala-lang.org/download/)
2. [Install sbt](http://www.scala-sbt.org/1.0/docs/Setup.html)
3. `git clone https://github.com/sharmapankaj2512/fast-resumable-downloader.git`
4. `cd fast-resumable-downloader`
5. `sbt clean compile`

#### Future Improvements
1. Try replace `HttpURLConnection` with `akka-http`

### License
Fast resumable downloader is licensed under the [MIT License](https://opensource.org/licenses/MIT).
