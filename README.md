# FM-android

<p align="center">
    <img src="img/screen.png" align="center" />
</p>

## Environment variables

`release` build requires to set the following environment variables. Those variables are set by Travis
automatically. In case of building `apk` on a local set the followings:

- `SERVER_CLIENT_ID`: used to authenticate with google account
- `STORE_PASSWORD`: store password
- `KEY_ALIAS`: key alias
- `KEY_PASSWORD`: key password


```bash
openssl aes-256-cbc -k "password" -salt -in keys/fm.jks -out keys/keys/fm.jks.enc
```
