# Simple crawler

Minimal application that provides REST API to extract title from web pages.
To start application you can use Intellij Idea, 
class `su.creator.simpleCrawler.SimpleCrawler`.

## REST API

Application has single endpoint that receives POST request with list
of URIs to handle. Response contains list of incoming URIs enriched with
either content of `head / title` HTML tag or with error message.

```
POST / HTTP/1.1
Accept: application/json, */*
Accept-Encoding: gzip, deflate
Connection: keep-alive
Content-Length: 87
Content-Type: application/json
Host: localhost:8080
User-Agent: HTTPie/0.9.9

{
    "uris": [
        "http://www.creator.su/",
        "https://github.com",
        "http://github.com/foo/bar"
    ]
}

HTTP/1.1 200 OK
Content-Length: 284
Content-Type: application/json
Date: Tue, 20 Nov 2018 00:16:52 GMT
Server: akka-http/10.1.5

{
    "items": [
        {
            "title": "Creator's blog | Изобретая велосипед",
            "uri": "http://www.creator.su/"
        },
        {
            "title": "The world’s leading software development platform · GitHub",
            "uri": "https://github.com"
        },
        {
            "error": "Bad response: 404 Not Found",
            "uri": "http://github.com/foo/bar"
        }
    ]
}
```