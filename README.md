# Keycloak OpenID Connect CLI

Keycloak OpenID Connect CLI provides a CLI interface to obtain tokens from an OpenID Connect provider.

Features include:

* Multiple configuration contexts to easily switch between different providers, flows, accounts, etc.
* Supports a range of different OAuth and OpenID Connect flows
* Decode JWT tokens into a human-readable JSON representation
* Invoke the providers token introspection endpoint to decode and verify tokens
* Invoke the providers user-info endpoint to obtain user information from an access token
* Integration with `kubectl`
* Token cache

The following flows are supported to obtain tokens:

* Authorization Code with PKCE: Uses the system browser to obtain tokens
* Client Credentials: Uses client credentials to obtain tokens for service accounts
* Device Authorization Grant: Enables login via an external device
* Password Grant: Uses username and password to obtain tokens. Not recommended for anything beyond development purposes. 

## Installation

### Linux

Run the following commands:
```
curl -L $(curl --silent https://api.github.com/repos/stianst/keycloak-oidc-cli/releases/latest | grep 'browser_download_url.*kc-oidc-linux-amd64' | cut -d '"' -f 4) -o kc-oidc
chmod +x kc-oidc
```

Then move `kc-oidc` to somewhere on the classpath.


## Configuration

Configuration is stored in `~/.kc/kc-oidc-config.yaml`, which can be edited manually, but it is more convinient to use the built-in
configuration commands.

To create a configuration context use `kc-oidc config set`, for example:

```
kc-oidc config set --context=mycontext --issuer=http://localhost:8080/realms/myrealm --flow=authorization-code --client-id=myclient
```

If there are multiple contexts available you can set the current default context with:

```
kc-oidc config use --context=mycontext
```

The following will view the current default context:

```
kc-oidc config current
```

To modify an existing context use `kc-oidc config update`, for example:

```
kc-oidc config update --context=mycontext --client-id=myotherclient
```

To unset a config field use `null` as the value:

```
kc-oidc config update --context=mycontext --client-id=null
```

### Environment variables

#### `KC_OIDC_CONF_FILE`

Specify an alternative configuration file. For example:

```
export KC_OIDC_CONF_FILE=/tmp/kc-oidc-tmp.yaml
kc-oidc config view
```

#### `KC_OIDC_TOKEN_CACHE_FILE`

Specify an alternative token cache file. For example:

```
export KC_OIDC_TOKEN_CACHE_FILE=/tmp/kc-oidc-tmp-cache.yaml
```

#### `KC_OIDC_BROWSER_CMD`

Specify an alternative command to open URLs in the system browser. For example:

```
export KC_OIDC_BROWSER_CMD="google-chrome --incognito"
kc-oidc token
```

## Fetching tokens

After you've created a configuration context simply run:

```
kc-oidc token
```

If there are active cached tokens these will be used, otherwise the configured flow will be executed to obtain
tokens from the OpenID Connect provider.

By default, the `Access Token` is printed in it's encoded format. Use `--decode` to view the decoded JWT, or to get the
`ID Token` use `--type=id`.

To use a different context to the default configuration context use `--context=<context name>`.


## Token cache

By default, tokens are cached in `~/.kc/kc-oidc-tokens.yaml` within the corresponding configuratino context. To not cache tokens
use `--store-tokens=false` when creating or updating a configuration context. 


## Browser support

Currently, only Linux and MacOS are supported for opening URLs through the system browser, which is required by the
authorization-code flow.

For other platforms it is possible to set an environment variable to a command that can open a URL:

```
export KC_OIDC_BROWSER_CMD="google-chrome --incognito"
```


## Kubernetes command line tool (kubectl)

`kc-oidc` can be used as a plugin to `kubectl` to enable seamless authentication to a 
[Kubernetes cluster configured to support OpenID Connect Tokens](https://kubernetes.io/docs/reference/access-authn-authz/authentication/#openid-connect-tokens) 
for authentication.

First step is to copy `kc-oidc` to `kubectl-kc` and make it available on the path. After that run the following to
verify it works:

```
kubectl kc token
```

Configure credentials for `kubectl` to enable using `kc-oidc` to obtain tokens. For example:

```
kubectl config set-credentials kc-oidc --exec-api-version=client.authentication.k8s.io/v1 --exec-command='kubectl' --exec-arg='kc' --exec-arg='token'
```

If you want to use a specific `kc-oidc` configuration context for `kubectl` add `--exec-arg='--context=<context name>' at the
end of the command above.

`kubectl config set-credentials` doesn't currently allow specifying `interactiveMode`, so you need to edit `.kube/config`, 
search for `kc-oidc`, and add `interactiveMode: IfAvailable` like shown below:

```
- name: kc-oidc
  user:
    exec:
      apiVersion: client.authentication.k8s.io/v1
      args:
      - kc
      - token
      command: kubectl
      env: null
      provideClusterInfo: false
      interactiveMode: IfAvailable
```

Next, you need to create a context entry that uses the previously configured credentials. For example:

```
kubectl config set-context kc-oidc --cluster=minikube --user kc-oidc
kubectl config use-context kc-oidc
```


## Build

### Uber JAR

Build:
```
mvn clean install
```

Run:
```
java -jar target/kc-oidc.jar
```

### Native executable

Build:
```
mvn clean install -Dnative
```

Run:
```
target/kc-oidc
```

See [building-native-image](https://quarkus.io/guides/building-native-image) for instructions on setting up GraalVM if needed.
