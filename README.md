# Hlæja Account Register

In twilight's hush, where mythic tales unfold, A ledger of legends, the bravest to behold. Each hero's name, etched in the annals of time, Their feats of strength, magic, and cunning, forever to shine. From dark forests to ancient ruins, guilds unite, Their banners waving, in the face of endless night. The Guild Codex, a sacred text of old, A testament to heroes, where courage never grows cold.

## Properties for deployment

| name                   | required | info                    |
|------------------------|:--------:|-------------------------|
| spring.profiles.active | &check;  | Spring Boot environment |
| spring.r2dbc.url       | &check;  | Postgres host url       |
| spring.r2dbc.username  | &check;  | Postgres username       |
| spring.r2dbc.password  | &cross;  | Postgres password       |

*Required: &check; can be stored as text, and &cross; need to be stored as secret.*

## Releasing Service

Run `release.sh` script from `master` branch.

## Development Information

### Global Setting

This services rely on a set of global settings to configure development environments. These settings, managed through Gradle properties or environment variables.

*Note: For more information on global properties, please refer to our [global settings](https://github.com/swordsteel/hlaeja-development/blob/master/doc/global_settings.md) documentation.*

#### Gradle Properties

```properties
repository.user=your_user
repository.token=your_token_value
```

#### Environment Variables

```properties
REPOSITORY_USER=your_user
REPOSITORY_TOKEN=your_token_value
```
