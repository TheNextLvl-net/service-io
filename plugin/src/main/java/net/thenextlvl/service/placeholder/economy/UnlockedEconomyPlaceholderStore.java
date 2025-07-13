package net.thenextlvl.service.placeholder.economy;

import net.milkbowl.vault2.economy.AccountPermission;
import net.milkbowl.vault2.economy.Economy;
import net.thenextlvl.service.ServicePlugin;
import net.thenextlvl.service.placeholder.api.PlaceholderResolver;
import net.thenextlvl.service.placeholder.api.PlaceholderStore;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

/**
 * <a href="https://github.com/TheNewEconomy/VaultUnlocked/blob/master/src/net/milkbowl/vault/papi/EconomyPlaceholders.java">Source</a>
 */
@NullMarked
public class UnlockedEconomyPlaceholderStore extends PlaceholderStore<Economy> {
    private static final String PLUGIN_NAME = "VaultUnlocked";

    public UnlockedEconomyPlaceholderStore(ServicePlugin plugin) {
        super(plugin, Economy.class);
    }

    @Override
    protected void registerResolvers(Economy provider) {
        // %vaultunlocked_balance_currency_<currency>%
        registerResolver("balance_currency_%s", (player, matcher) -> {
            var currency = decode(matcher.group(1));
            return provider.balance(PLUGIN_NAME, player.getUniqueId(), "world", currency).toPlainString();
        });

        // %vaultunlocked_balance_currency_<currency>_world_<world>%
        registerResolver("balance_currency_%s_world_%s", (player, matcher) -> {
            var currency = decode(matcher.group(1));
            var world = matcher.group(2);
            return provider.balance(PLUGIN_NAME, player.getUniqueId(), world, currency).toPlainString();
        });

        // %vaultunlocked_balance_<world>%
        registerResolver("balance_%s", (player, matcher) -> {
            var world = matcher.group(1);
            return provider.balance(PLUGIN_NAME, player.getUniqueId(), world).toPlainString();
        });

        // %vaultunlocked_balance%
        registerResolver("balance", (player, matcher) -> {
            return provider.balance(PLUGIN_NAME, player.getUniqueId()).toPlainString();
        });

        // %vaultunlocked_balanceformatted_currency_<currency>%
        registerResolver("balanceformatted_currency_%s", (player, matcher) -> {
            var currency = decode(matcher.group(1));
            return provider.format(PLUGIN_NAME, provider.balance(PLUGIN_NAME, player.getUniqueId(), "world", currency));
        });

        // %vaultunlocked_balanceformatted_currency_<currency>_world_<world>%
        registerResolver("balanceformatted_currency_%s_world_%s", (player, matcher) -> {
            var currency = decode(matcher.group(1));
            var world = matcher.group(2);
            return provider.format(PLUGIN_NAME, provider.balance(PLUGIN_NAME, player.getUniqueId(), world, currency));
        });

        // %vaultunlocked_balanceformatted_<world>% // doesn't exist in the original
        registerResolver("balanceformatted_%s", (player, matcher) -> {
            var world = matcher.group(1);
            return provider.format(PLUGIN_NAME, provider.balance(PLUGIN_NAME, player.getUniqueId(), world));
        });

        // %vaultunlocked_balanceformatted%
        registerResolver("balanceformatted", (player, matcher) -> {
            return provider.format(PLUGIN_NAME, provider.balance(PLUGIN_NAME, player.getUniqueId()));
        });

        // %vaultunlocked_account_<uuid>%
        registerResolver("account_%s", PlaceholderResolver.throwing((player, matcher) -> {
            var accountId = UUID.fromString(matcher.group(1));
            return provider.balance(PLUGIN_NAME, accountId).toPlainString();
        }, IllegalArgumentException.class));

        // %vaultunlocked_account_<uuid>_currency_<currency>%
        registerResolver("account_%s_currency_%s", PlaceholderResolver.throwing((player, matcher) -> {
            var accountId = UUID.fromString(matcher.group(1));
            var currency = decode(matcher.group(2));
            return provider.balance(PLUGIN_NAME, accountId, "world", currency).toPlainString();
        }, IllegalArgumentException.class));

        // %vaultunlocked_account_<uuid>_currency_<currency>_formatted%
        registerResolver("account_%s_currency_%s_formatted", PlaceholderResolver.throwing((player, matcher) -> {
            var accountId = UUID.fromString(matcher.group(1));
            var currency = decode(matcher.group(2));
            return provider.format(PLUGIN_NAME, provider.balance(PLUGIN_NAME, accountId, "world", currency));
        }, IllegalArgumentException.class));

        // %vaultunlocked_account_<uuid>_currency_<currency>_world_<world>%
        registerResolver("account_%s_currency_%s_world_%s", PlaceholderResolver.throwing((player, matcher) -> {
            var accountId = UUID.fromString(matcher.group(1));
            var currency = decode(matcher.group(2));
            var world = matcher.group(3);
            return provider.balance(PLUGIN_NAME, accountId, world, currency).toPlainString();
        }, IllegalArgumentException.class));

        // %vaultunlocked_account_<uuid>_currency_<currency>_world_<world>_formatted%
        registerResolver("account_%s_currency_%s_world_%s", PlaceholderResolver.throwing((player, matcher) -> {
            var accountId = UUID.fromString(matcher.group(1));
            var currency = decode(matcher.group(2));
            var world = matcher.group(3);
            return provider.format(PLUGIN_NAME, provider.balance(PLUGIN_NAME, accountId, world, currency));
        }, IllegalArgumentException.class));

        registerResolver("can_%s_%s", PlaceholderResolver.throwing((player, matcher) -> {
            var accountId = UUID.fromString(matcher.group(2));
            var permission = switch (matcher.group(1)) {
                case "deposit" -> AccountPermission.DEPOSIT; // %vaultunlocked_can_deposit_<uuid>%
                case "withdraw" -> AccountPermission.WITHDRAW; // %vaultunlocked_can_withdraw_<uuid>%
                case "balance" -> AccountPermission.BALANCE; // %vaultunlocked_can_balance_<uuid>%
                case "transfer" -> AccountPermission.TRANSFER_OWNERSHIP; // %vaultunlocked_can_transfer_<uuid>%
                case "invite" -> AccountPermission.INVITE_MEMBER; // %vaultunlocked_can_invite_<uuid>%
                case "remove" -> AccountPermission.REMOVE_MEMBER; // %vaultunlocked_can_remove_<uuid>%
                case "modify" -> AccountPermission.CHANGE_MEMBER_PERMISSION; // %vaultunlocked_can_modify_<uuid>%
                case "delete" -> AccountPermission.DELETE; // %vaultunlocked_can_delete_<uuid>%
                default -> null;
            };
            if (permission == null) return null;
            return provider.hasAccountPermission(PLUGIN_NAME, accountId, player.getUniqueId(), permission) ? "yes" : "no";
        }, IllegalArgumentException.class));

        // %vaultunlocked_accounts%
        registerResolver("accounts", (player, matcher) -> {
            return String.join(", ", provider.accountsMemberOf(PLUGIN_NAME, player.getUniqueId()));
        });

        // %vaultunlocked_accounts_count%
        registerResolver("accounts_count", (player, matcher) -> {
            return String.valueOf(provider.accountsMemberOf(PLUGIN_NAME, player.getUniqueId()).size());
        });

        // %vaultunlocked_currency%
        registerResolver("currency", (uuid, params) -> {
            return provider.defaultCurrencyNameSingular(PLUGIN_NAME);
        });

        // %vaultunlocked_currencyplural%
        registerResolver("currencyplural", (uuid, params) -> {
            return provider.defaultCurrencyNamePlural(PLUGIN_NAME);
        });
    }

    private String decode(final String input) {
        return input.replace("%20", " ")
                .replace("%24", "$")
                .replace("%2B", "+")
                .replace("%26", "&")
                .replace("%2F", "/")
                .replace("%3D", "=");
    }
}
