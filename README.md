# RSS Reader

Concurrent real-time RSS/Atom feed receiver

## Requirements

* JDK 11
* Apache Maven 3

## Build

Execute Maven command listed below. It will compile code, run tests and assembly jar package with dependencies.
```sh
mvn clean package
```
Or you can just download compiled jar package on [Releases](https://github.com/ivyanni/rssreader/releases) page

## Usage example

Execute command
```sh
java -jar rssreader-final.jar
```

### Available commands
* _add_ - create new feed subscription
* _list_ - show existing active feeds
* _modify_ - change properties for existing feed
* _remove_ - remove existing feed
* _exit_ - shutdown application saving current configuration

## Release History

* 0.1
    * First release

## Licencse

Distributed under the GPL-3.0 license. See ``LICENSE`` for more information.
