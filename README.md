# ErrorCollector Logback-classic ![Maven metadata URL](https://img.shields.io/maven-metadata/v/https/repo.maven.apache.org/maven2/net/mayope/errorcollector/maven-metadata.xml.svg?label=mavenCentral)

This projects aims to provide a convenient way to be notified if an error in on of your systems occurs.

The appender will collect all errors in a configurable interval (default 5 min).

If exception occurred will send them to the configured publisher(Microsoft Teams and Telegram are currently supported)
## Gradle
kotlinscript:
```groovy
repositories{
  mavenCentral()
}
dependencies{
  implementation("net.mayope:errorcollector:x.x.x")
}
```
groovy:
```groovy
repositories{
  mavenCentral()
}
dependencies{
  implementation 'net.mayope:errorcollector:x.x.x'
}
```

## Maven
```xml
<dependency>
  <groupId>net.mayope</groupId>
  <artifactId>errorcollector</artifactId>
  <version>0.0.7</version>
</dependency>
```

## Usage Telegram
Example Logback configuration
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <appender name="errorAppender" class="net.mayope.errorcollector.TelegramAppender">
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>ERROR</level>
        </filter>
        <url>https://api.telegram.org</url>
        <chatId>-5514719561</chatId>
        <botToken>1711031898:AAEtyASDJwnl73or02KLttD3zPlWWIPuxVs</botToken>
      
        <urlPastebin>https://my.deployed.pastebin</urlPastebin>
        <pastebinUsername>username</pastebinUsername>
        <pastebinPassword>password</pastebinPassword>
        <blacklist>exception1;exception2</blacklist>
        <connectTimeOut>60</connectTimeOut>
        <readTimeOut>600</readTimeOut>
        <issueProvider>github</issueProvider>
        <issueBaseUrl>https://github.com/mayope/errorcollector/issues/new</issueBaseUrl>
        <sendIntervalMinutes>https://github.com/mayope/errorcollector/issues/new</sendIntervalMinutes>
        <serviceName>myCoolService</serviceName>
        <activateOnEnv>PRODUCTION</activateOnEnv>
    </appender>

    <root level="ERROR">
        <appender-ref ref="errorAppender"/>
    </root>
</configuration>
```
### Parameter
- url: String, optional, default = "https://api.telegram.org"
- chatId: String, required 
- botToken: String, required
  
- connectTimeOut: Long, optional, default = 60ms
- readTimeOut: Long, optional, default = 600ms
  

- urlPastebin: String?, optional, default = null

    | this can be used to post stacktraces to a [pastebin](https://github.com/mkaczanowski/pastebin).
    In the message will then appear a link to the pastebin for this stacktrace


- pastebinUsername: String?, optional, default = null
- pastebinPassword: String?, optional, default = null

    | This parameters can be used for basic authentication against the configured pastebin


- blacklist: String = "", optional, default = null

  | Will split all strings in this parameter by `;` and if an exception contains any of this strings, it will be dropped


- issueProvider: String?, optional, default = null, possible values= `jira`,`github`

  | this parameter can be used to create an issue create link in the message 

- issueBaseUrl: String?, optional, default = null
  

- sendIntervalMinutes: Long, optional, default = 5
- serviceName: String?, optional, default = Environment variable `HOSTNAME` or if this is also null `""`
- activateOnEnv: String?, optional ,default = null

  | Only enable error sending if this environment variable is set to any value

## Usage Teams
Example Logback Configuration

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

  <appender name="teamsAppender" class="net.mayope.errorcollector.TeamsAppender">
    <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
      <level>ERROR</level>
    </filter>
    <webhookUrl>https://outlook.office.com/webhook/456654612321651</webhookUrl>
    
    <urlPastebin>https://my.deployed.pastebin</urlPastebin>
    <pastebinUsername>username</pastebinUsername>
    <pastebinPassword>password</pastebinPassword>
    <blacklist>exception1;exception2</blacklist>
    <connectTimeOut>60</connectTimeOut>
    <readTimeOut>600</readTimeOut>
    <issueProvider>github</issueProvider>
    <issueBaseUrl>https://github.com/mayope/errorcollector/issues/new</issueBaseUrl>
    <sendIntervalMinutes>https://github.com/mayope/errorcollector/issues/new</sendIntervalMinutes>
    <serviceName>myCoolService</serviceName>
    <activateOnEnv>PRODUCTION</activateOnEnv>
  </appender>

  <include resource="org/springframework/boot/logging/logback/base.xml"/>
  <logger name="org.springframework" level="INFO"/>

  <root level="ERROR">
    <appender-ref ref="teamsAppender"/>
  </root>
</configuration>
```

## Parameter

- webhookUrl: String, required

Following parameters are the same as for the Telegram Appender
