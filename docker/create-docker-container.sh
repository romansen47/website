#!/bin/bash

docker build -t chess .
docker run -p 80:80 443:443 chess
