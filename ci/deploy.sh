#!/bin/sh

# decrypt signatures
openssl aes-256-cbc -d -k "$FILE_PASSWORD" -in keys/fm.jks.enc -out fm.jks
openssl aes-256-cbc -d -k "$FILE_PASSWORD" -in keys/play-store.p12.enc -out play-store.p12
