# ServiceIO

A modern, drop-in replacement for Vault that offers enhanced functionality,
better performance, and seamless integration across plugins.

> [!NOTE]
> ServiceIO only supports recent builds of [paper](https://papermc.io/downloads/paper) (1.21+)
>
> ServiceIO is designed as a full replacement for Vault, so Vault is no longer necessary when you make the switch. Rest
> assured, all your existing plugins that depend on Vault will continue to function seamlessly with ServiceIO.

Metrics can be found [here](https://bstats.org/plugin/bukkit/TheNextLvl%20ServiceIO/23083)

## Why You should use ServiceIO?

ServiceIO is a cutting-edge alternative to Vault, designed to overcome the limitations of the outdated VaultAPI. While
Vault still serves a purpose, it relies on deprecated and unsupported code, forcing developers to work with null-unsafe
interfaces that can lead to instability and errors.

ServiceIO addresses these issues by offering robust nullability annotations, comprehensive documentation, and
well-maintained source code. In addition, ServiceIO enables asynchronous data loading and provides access to cached
results, enhancing performance and scalability.

A key feature of ServiceIO is its seamless integration with existing Vault interfaces, allowing your plugins to utilize
both APIs simultaneously without requiring any additional code changes. This ensures a smooth transition while
leveraging the benefits of a modern, reliable platform.

Moreover, ServiceIO goes beyond what Vault offers by enabling data conversion between different plugins, not just for
economy but also for banks, permissions, chat, and groups. This eliminates the need for developers to implement custom
conversion logic, saving time and effort. For server owners, this means the ability to switch out plugins more
seamlessly than ever before, ensuring smooth operation and flexibility without the headaches of manual data migration.

## Commands

### Convert

| Command                                        | Description                                                                              |
|------------------------------------------------|------------------------------------------------------------------------------------------|
| /service convert banks <source> <target>       | Convert all banks and bank accounts                                                      |
| /service convert chat <source> <target>        | Convert all chat data (prefixes, suffixes, display names...)                             |
| /service convert economy <source> <target>     | Convert all economy data (accounts and balances)                                         |
| /service convert groups <source> <target>      | Convert all group data (groups, prefixes, suffixes, display names, permissions, members) |
| /service convert permissions <source> <target> | Convert all permission data (users, permissions)                                         |

### Info

| Command                   | Description                                        |
|---------------------------|----------------------------------------------------|
| /service info             | See all plugins that add any kind of functionality |
| /service info bank        | See what bank provider plugins are installed       |
| /service info chat        | See what chat provider plugins are installed       |
| /service info economy     | See what economy provider plugins are installed    |
| /service info groups      | See what group provider plugins are installed      |
| /service info permissions | See what permission provider plugins are installed |

## Permissions

To use the `/service convert` command, the permission `service.convert` is required<br/>
To use the `/service info` command, the permission `service.info` is required

The `service.admin` permission grants access to all ServiceIO commands

For compatibility reasons `vault.admin` acts as `service.admin`

## API

To include the API in your Gradle project using Kotlin DSL, follow these steps:

1. **Add the repository:** Include the `maven` repository in your `repositories` block.
2. **Add the dependency:** Replace `<version>` with the actual version of the API.

Here's an example configuration:

```kts

repositories {
    maven("https://repo.thenextlvl.net/releases")
}

dependencies {
    compileOnly("net.thenextlvl.services:service-io:<version>")
}
```

To find the latest version:

1. Visit the [repository link](https://repo.thenextlvl.net/#/releases/net/thenextlvl/services/service-io).
2. Replace `<version>` in your dependency declaration with the latest version number listed.

For example, if the latest version is **0.1.0**, your dependency would look like this:

```kts
dependencies {
    compileOnly("net.thenextlvl.services:service-io:0.1.0")
}
```

You can find both sources and javadocs on the repository<br/>
Also you can download the docs from within your IDE

### Implementing ServiceIO

ServiceIO _loosely_ follows the Model View Controller (MVC) design pattern<br>
At the moment there are following controllers:

    BankController
    ChatController
    EconomyController
    GroupController
    PermissionController

Controllers are responsible for loading, retrieving, deleting and creating data

Example on how to access controllers:

```java

public @Nullable BankController getBankController() {
    return getServer().getServicesManager().load(BankController.class);
}

public @Nullable ChatController getChatController() {
    return getServer().getServicesManager().load(ChatController.class);
}

public @Nullable EconomyController getEconomyController() {
    return getServer().getServicesManager().load(EconomyController.class);
}
```

Example on how to provide your own controllers:

```java
private void registerBankController() {
    getServer().getServicesManager().register(BankController.class, new YourBankController(), this, ServicePriority.Highest);
}

private void registerEconomyController() {
    getServer().getServicesManager().register(EconomyController.class, new YourEconomyController(), this, ServicePriority.Highest);
}
```
