package net.thenextlvl.service.plugin.wrapper.vaultunlocked;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.milkbowl.vault2.economy.AccountPermission;
import net.milkbowl.vault2.economy.Economy;
import net.milkbowl.vault2.economy.EconomyResponse;
import net.thenextlvl.service.economy.Account;
import net.thenextlvl.service.economy.EconomyController;
import net.thenextlvl.service.economy.TransactionResult;
import net.thenextlvl.service.economy.bank.BankController;
import net.thenextlvl.service.economy.currency.Currency;
import net.thenextlvl.service.plugin.wrapper.Wrapper;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static net.milkbowl.vault2.economy.EconomyResponse.ResponseType.FAILURE;
import static net.milkbowl.vault2.economy.EconomyResponse.ResponseType.SUCCESS;

public final class VaultUnlockedEconomyServiceWrapper implements Economy, Wrapper {
    private final @Nullable BankController bankController;
    private final EconomyController economyController;
    private final Plugin plugin;

    public VaultUnlockedEconomyServiceWrapper(final EconomyController economyController, final Plugin plugin) {
        this.bankController = plugin.getServer().getServicesManager().load(BankController.class);
        this.economyController = economyController;
        this.plugin = plugin;
    }

    private Currency getCurrency() {
        return economyController.getCurrencyController().getDefaultCurrency();
    }

    private Optional<Currency> getCurrency(final String name) {
        return economyController.getCurrencyController().getCurrency(name);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        if (economyController instanceof Wrapper) return economyController.getName();
        return economyController.getName() + " Wrapper";
    }

    @Override
    public boolean hasSharedAccountSupport() {
        return false;
    }

    @Override
    public boolean hasMultiCurrencySupport() {
        return economyController.getCurrencyController().getCurrencies().size() > 1;
    }

    @Override
    public int fractionalDigits(final String pluginName) {
        return getCurrency().getFractionalDigits();
    }

    @Override
    public String format(final BigDecimal amount) {
        return PlainTextComponentSerializer.plainText().serialize(getCurrency().format(amount, Locale.US));
    }

    @Override
    public String format(final String pluginName, final BigDecimal amount) {
        return format(amount);
    }

    @Override
    public String format(final BigDecimal amount, final String currency) {
        return getCurrency(currency)
                .map(c -> PlainTextComponentSerializer.plainText().serialize(c.format(amount, Locale.US)))
                .orElseGet(() -> format(amount));
    }

    @Override
    public String format(final String pluginName, final BigDecimal amount, final String currency) {
        return format(amount, currency);
    }

    @Override
    public boolean hasCurrency(final String currency) {
        return getCurrency(currency).isPresent();
    }

    @Override
    public String getDefaultCurrency(final String pluginName) {
        return getCurrency().getName();
    }

    @Override
    public String defaultCurrencyNamePlural(final String pluginName) {
        return getCurrency().getDisplayNamePlural(Locale.US)
                .map(PlainTextComponentSerializer.plainText()::serialize)
                .orElse("");
    }

    @Override
    public String defaultCurrencyNameSingular(final String pluginName) {
        return getCurrency().getDisplayNameSingular(Locale.US)
                .map(PlainTextComponentSerializer.plainText()::serialize)
                .orElse("");
    }

    @Override
    public Collection<String> currencies() {
        return economyController.getCurrencyController().getCurrencies().stream()
                .map(Currency::getName)
                .toList();
    }

    @Override
    public boolean createAccount(final UUID accountID, final String name) {
        return tryResolve(economyController.createAccount(accountID), null) != null;
    }

    @Override
    public boolean createAccount(final UUID accountID, final String name, final boolean player) {
        return createAccount(accountID, name);
    }

    @Override
    public boolean createAccount(final UUID accountID, final String name, final String worldName) {
        return Optional.ofNullable(plugin.getServer().getWorld(worldName))
                .map(world -> tryResolve(economyController.createAccount(accountID, world), null))
                .or(() -> Optional.ofNullable(tryResolve(economyController.createAccount(accountID), null)))
                .isPresent();
    }

    @Override
    public boolean createAccount(final UUID accountID, final String name, final String worldName, final boolean player) {
        return createAccount(accountID, name, worldName);
    }

    @Override
    public Map<UUID, String> getUUIDNameMap() {
        return Map.of();
    }

    @Override
    public Optional<String> getAccountName(final UUID accountID) {
        return economyController.getAccount(accountID)
                .map(account -> plugin.getServer().getOfflinePlayer(account.getOwner()).getName());
    }

    @Override
    public boolean hasAccount(final UUID accountID) {
        return economyController.getAccount(accountID).isPresent();
    }

    @Override
    public boolean hasAccount(final UUID accountID, final String worldName) {
        return Optional.ofNullable(plugin.getServer().getWorld(worldName))
                .flatMap(world -> economyController.getAccount(accountID, world))
                .or(() -> economyController.getAccount(accountID))
                .isPresent();
    }

    @Override
    public boolean renameAccount(final UUID accountID, final String name) {
        return false;
    }

    @Override
    public boolean renameAccount(final String plugin, final UUID accountID, final String name) {
        return false;
    }

    @Override
    public boolean deleteAccount(final String plugin, final UUID accountID) {
        return tryResolve(economyController.deleteAccount(accountID), false);
    }

    @Override
    public boolean accountSupportsCurrency(final String plugin, final UUID accountID, final String currency) {
        return economyController.getAccount(accountID)
                .map(account -> getCurrency(currency).map(account::canHold).orElse(false))
                .orElse(false);
    }

    @Override
    public boolean accountSupportsCurrency(final String plugin, final UUID accountID, final String currency, final String world) {
        return Optional.ofNullable(this.plugin.getServer().getWorld(world))
                .flatMap(w -> economyController.getAccount(accountID, w))
                .or(() -> economyController.getAccount(accountID))
                .map(account -> getCurrency(currency).map(account::canHold).orElse(false))
                .orElse(false);
    }

    @Override
    public BigDecimal getBalance(final String pluginName, final UUID accountID) {
        return getAccount(accountID, null)
                .map(account -> account.getBalance(getCurrency()))
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public BigDecimal getBalance(final String pluginName, final UUID accountID, final String world) {
        return getAccount(accountID, world)
                .map(account -> account.getBalance(getCurrency()))
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public BigDecimal getBalance(final String pluginName, final UUID accountID, final String world, final String currency) {
        return getAccount(accountID, world)
                .map(account -> getCurrency(currency)
                        .map(account::getBalance)
                        .orElseGet(() -> account.getBalance(getCurrency())))
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public boolean has(final String pluginName, final UUID accountID, final BigDecimal amount) {
        return getBalance(pluginName, accountID).compareTo(amount) >= 0;
    }

    @Override
    public boolean has(final String pluginName, final UUID accountID, final String worldName, final BigDecimal amount) {
        return getBalance(pluginName, accountID, worldName).compareTo(amount) >= 0;
    }

    @Override
    public boolean has(final String pluginName, final UUID accountID, final String worldName, final String currency, final BigDecimal amount) {
        return getBalance(pluginName, accountID, worldName, currency).compareTo(amount) >= 0;
    }

    @Override
    public EconomyResponse withdraw(final String pluginName, final UUID accountID, final BigDecimal amount) {
        return getAccount(accountID, null)
                .map(account -> transformTransaction(amount, account.withdraw(amount, getCurrency())))
                .orElseGet(() -> new EconomyResponse(amount, BigDecimal.ZERO, FAILURE, "Account not found"));
    }

    @Override
    public EconomyResponse withdraw(final String pluginName, final UUID accountID, final String worldName, final BigDecimal amount) {
        return getAccount(accountID, worldName)
                .map(account -> transformTransaction(amount, account.withdraw(amount, getCurrency())))
                .orElseGet(() -> new EconomyResponse(amount, BigDecimal.ZERO, FAILURE, "Account not found"));
    }

    @Override
    public EconomyResponse withdraw(final String pluginName, final UUID accountID, final String worldName, final String currency, final BigDecimal amount) {
        return getAccount(accountID, worldName)
                .map(account -> getCurrency(currency)
                        .map(c -> transformTransaction(amount, account.withdraw(amount, c)))
                        .orElseGet(() -> new EconomyResponse(amount, BigDecimal.ZERO, FAILURE, "Currency not found")))
                .orElseGet(() -> new EconomyResponse(amount, BigDecimal.ZERO, FAILURE, "Account not found"));
    }

    @Override
    public EconomyResponse deposit(final String pluginName, final UUID accountID, final BigDecimal amount) {
        return getAccount(accountID, null)
                .map(account -> transformTransaction(amount, account.deposit(amount, getCurrency())))
                .orElseGet(() -> new EconomyResponse(amount, BigDecimal.ZERO, FAILURE, "Account not found"));
    }

    @Override
    public EconomyResponse deposit(final String pluginName, final UUID accountID, final String worldName, final BigDecimal amount) {
        return getAccount(accountID, worldName)
                .map(account -> transformTransaction(amount, account.deposit(amount, getCurrency())))
                .orElseGet(() -> new EconomyResponse(amount, BigDecimal.ZERO, FAILURE, "Account not found"));
    }

    @Override
    public EconomyResponse deposit(final String pluginName, final UUID accountID, final String worldName, final String currency, final BigDecimal amount) {
        return getAccount(accountID, worldName)
                .map(account -> getCurrency(currency)
                        .map(c -> transformTransaction(amount, account.deposit(amount, c)))
                        .orElseGet(() -> new EconomyResponse(amount, BigDecimal.ZERO, FAILURE, "Currency not found")))
                .orElseGet(() -> new EconomyResponse(amount, BigDecimal.ZERO, FAILURE, "Account not found"));
    }

    @Override
    public boolean createSharedAccount(final String pluginName, final UUID accountID, final String name, final UUID owner) {
        return false;
    }

    @Override
    public boolean isAccountOwner(final String pluginName, final UUID accountID, final UUID uuid) {
        return accountID.equals(uuid);
    }

    @Override
    public boolean setOwner(final String pluginName, final UUID accountID, final UUID uuid) {
        return false;
    }

    @Override
    public boolean isAccountMember(final String pluginName, final UUID accountID, final UUID uuid) {
        return isAccountOwner(pluginName, accountID, uuid);
    }

    @Override
    public boolean addAccountMember(final String pluginName, final UUID accountID, final UUID uuid) {
        return false;
    }

    @Override
    public boolean addAccountMember(final String pluginName, final UUID accountID, final UUID uuid, final AccountPermission... initialPermissions) {
        return false;
    }

    @Override
    public boolean removeAccountMember(final String pluginName, final UUID accountID, final UUID uuid) {
        return false;
    }

    @Override
    public boolean hasAccountPermission(final String pluginName, final UUID accountID, final UUID uuid, final AccountPermission permission) {
        return false;
    }

    @Override
    public boolean updateAccountPermission(final String pluginName, final UUID accountID, final UUID uuid, final AccountPermission permission, final boolean value) {
        return false;
    }

    private Optional<BankController> getBankController() {
        return Optional.ofNullable(bankController);
    }

    private Optional<Account> getAccount(final UUID uuid, @Nullable final String worldName) {
        return Optional.ofNullable(worldName)
                .map(plugin.getServer()::getWorld)
                .flatMap(world -> economyController.getAccount(uuid, world))
                .or(() -> economyController.getAccount(uuid));
    }

    private EconomyResponse transformTransaction(final BigDecimal amount, final TransactionResult result) {
        final var responseType = switch (result.status()) {
            case CURRENCY_NOT_SUPPORTED, FAILURE, INSUFFICIENT_FUNDS -> FAILURE;
            case SUCCESS -> SUCCESS;
        };
        final var errorMessage = switch (result.status()) {
            case CURRENCY_NOT_SUPPORTED -> "Currency not supported";
            case INSUFFICIENT_FUNDS -> "Insufficient funds";
            default -> "";
        };
        return new EconomyResponse(amount, new BigDecimal(result.balance().toString()), responseType, errorMessage);
    }

    @FunctionalInterface
    public interface ThrowingFunction<T> {
        T apply(BankController controller) throws Exception;
    }

    private <T> T resolve(final CompletableFuture<T> future) throws ExecutionException, InterruptedException, TimeoutException {
        return future.get(5, TimeUnit.SECONDS);
    }

    @Contract("_, !null -> !null")
    private <T> @Nullable T tryResolve(final CompletableFuture<T> future, @Nullable final T defaultValue) {
        try {
            return resolve(future);
        } catch (final InterruptedException | ExecutionException | TimeoutException e) {
            plugin.getComponentLogger().error("Failed to get future synchronously", e);
            return defaultValue;
        }
    }
}
