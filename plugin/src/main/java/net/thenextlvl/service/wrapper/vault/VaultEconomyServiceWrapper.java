package net.thenextlvl.service.wrapper.vault;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.thenextlvl.service.api.economy.Account;
import net.thenextlvl.service.api.economy.EconomyController;
import net.thenextlvl.service.api.economy.TransactionResult;
import net.thenextlvl.service.api.economy.bank.Bank;
import net.thenextlvl.service.api.economy.bank.BankController;
import net.thenextlvl.service.api.economy.currency.Currency;
import net.thenextlvl.service.wrapper.Wrapper;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static net.milkbowl.vault.economy.EconomyResponse.ResponseType.FAILURE;
import static net.milkbowl.vault.economy.EconomyResponse.ResponseType.NOT_IMPLEMENTED;
import static net.milkbowl.vault.economy.EconomyResponse.ResponseType.SUCCESS;

@NullMarked
public final class VaultEconomyServiceWrapper implements Economy, Wrapper {
    private final @Nullable BankController bankController;
    private final EconomyController economyController;
    private final Plugin plugin;

    public VaultEconomyServiceWrapper(final EconomyController economyController, final Plugin plugin) {
        this.bankController = plugin.getServer().getServicesManager().load(BankController.class);
        this.economyController = economyController;
        this.plugin = plugin;
    }

    private Currency getCurrency() {
        return economyController.getCurrencyController().getDefaultCurrency();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return economyController.getName() + " Wrapper";
    }

    private Optional<BankController> getBankController() {
        return Optional.ofNullable(bankController);
    }

    @Override
    public boolean hasBankSupport() {
        return bankController != null;
    }

    @Override
    public int fractionalDigits() {
        return getCurrency().getFractionalDigits();
    }

    @Override
    public String format(final double amount) {
        return PlainTextComponentSerializer.plainText().serialize(getCurrency().format(amount, Locale.US));
    }

    @Override
    public String currencyNamePlural() {
        return getCurrency().getDisplayNamePlural(Locale.US)
                .map(PlainTextComponentSerializer.plainText()::serialize)
                .orElse("");
    }

    @Override
    public String currencyNameSingular() {
        return getCurrency().getDisplayNameSingular(Locale.US)
                .map(PlainTextComponentSerializer.plainText()::serialize)
                .orElse("");
    }

    @Override
    public boolean hasAccount(@Nullable final String name) {
        return hasAccount(name, null);
    }

    @Override
    public boolean hasAccount(@Nullable final OfflinePlayer player) {
        return hasAccount(player, null);
    }

    @Override
    public boolean hasAccount(@Nullable final String playerName, @Nullable final String worldName) {
        final var player = playerName != null ? plugin.getServer().getOfflinePlayerIfCached(playerName) : null;
        return hasAccount(player, worldName);
    }

    @Override
    public boolean hasAccount(@Nullable final OfflinePlayer player, @Nullable final String worldName) {
        return getAccount(player, worldName).isPresent();
    }

    @Override
    public double getBalance(@Nullable final String name) {
        return getBalance(name, null);
    }

    @Override
    public double getBalance(@Nullable final OfflinePlayer player) {
        return getBalance(player, null);
    }

    @Override
    public double getBalance(@Nullable final String playerName, @Nullable final String worldName) {
        final var player = playerName != null ? plugin.getServer().getOfflinePlayerIfCached(playerName) : null;
        return getBalance(player, worldName);
    }

    @Override
    public double getBalance(@Nullable final OfflinePlayer player, @Nullable final String worldName) {
        return getAccount(player, worldName)
                .map(account -> account.getBalance(getCurrency()))
                .map(Number::doubleValue)
                .orElse(0.0);
    }

    @Override
    public boolean has(@Nullable final String name, final double amount) {
        return getBalance(name) >= amount;
    }

    @Override
    public boolean has(@Nullable final OfflinePlayer player, final double amount) {
        return getBalance(player) >= amount;
    }

    @Override
    public boolean has(@Nullable final String name, @Nullable final String worldName, final double amount) {
        return getBalance(name, worldName) >= amount;
    }

    @Override
    public boolean has(@Nullable final OfflinePlayer player, @Nullable final String worldName, final double amount) {
        return getBalance(player, worldName) >= amount;
    }

    @Override
    public EconomyResponse withdrawPlayer(@Nullable final String name, final double amount) {
        return withdrawPlayer(name, null, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(@Nullable final OfflinePlayer player, final double amount) {
        return withdrawPlayer(player, null, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(@Nullable final String playerName, @Nullable final String worldName, final double amount) {
        final var player = playerName != null ? plugin.getServer().getOfflinePlayerIfCached(playerName) : null;
        return withdrawPlayer(player, worldName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(@Nullable final OfflinePlayer player, @Nullable final String worldName, final double amount) {
        return getAccount(player, worldName)
                .map(account -> transformTransaction(amount, account.withdraw(amount, getCurrency())))
                .orElseGet(() -> new EconomyResponse(amount, 0, FAILURE, "Player has no account"));
    }

    @Override
    public EconomyResponse depositPlayer(@Nullable final String name, final double amount) {
        return depositPlayer(name, null, amount);
    }

    @Override
    public EconomyResponse depositPlayer(@Nullable final OfflinePlayer player, final double amount) {
        return depositPlayer(player, null, amount);
    }

    @Override
    public EconomyResponse depositPlayer(@Nullable final String name, @Nullable final String worldName, final double amount) {
        final var player = name != null ? plugin.getServer().getOfflinePlayerIfCached(name) : null;
        return depositPlayer(player, worldName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(@Nullable final OfflinePlayer player, @Nullable final String worldName, final double amount) {
        return getAccount(player, worldName)
                .map(account -> transformTransaction(amount, account.deposit(amount, getCurrency())))
                .orElseGet(() -> new EconomyResponse(amount, 0, FAILURE, "Player has no account"));
    }

    @Override
    public EconomyResponse createBank(final String name, @Nullable final String playerName) {
        final var player = playerName != null ? plugin.getServer().getOfflinePlayerIfCached(playerName) : null;
        return createBank(name, player);
    }

    @Override
    public EconomyResponse createBank(final String name, @Nullable final OfflinePlayer player) {
        return getBankController(controller -> {
            if (player == null) return new EconomyResponse(0, 0, FAILURE, "Player not found");
            resolve(controller.createBank(player, name));
            return new EconomyResponse(0, 0, SUCCESS, null);
        });
    }

    @Override
    public EconomyResponse deleteBank(final String name) {
        return getBankController(controller -> {
            final var deleted = resolve(controller.deleteBank(name));
            return new EconomyResponse(0, 0, deleted ? SUCCESS : FAILURE, null);
        });
    }

    @Override
    public EconomyResponse bankBalance(final String name) {
        return getBankController(controller -> resolve(controller.resolveBank(name)).map(bank -> {
            return new EconomyResponse(0, bank.getBalance(getCurrency()).doubleValue(), SUCCESS, null);
        }).orElseGet(() -> new EconomyResponse(0, 0, FAILURE, "Bank not found")));
    }

    @Override
    public EconomyResponse bankHas(final String name, final double amount) {
        return getBankController(controller -> resolve(controller.resolveBank(name)).map(bank -> {
            final var balance = bank.getBalance(getCurrency()).doubleValue();
            final var response = balance >= amount ? SUCCESS : FAILURE;
            return new EconomyResponse(0, balance, response, null);
        }).orElseGet(() -> new EconomyResponse(0, 0, FAILURE, "Bank not found")));
    }

    @Override
    public EconomyResponse bankWithdraw(final String name, final double amount) {
        return getBankController(controller -> resolve(controller.resolveBank(name))
                .map(bank -> transformTransaction(amount, bank.withdraw(amount, getCurrency())))
                .orElseGet(() -> new EconomyResponse(0, 0, FAILURE, "Bank not found")));
    }

    @Override
    public EconomyResponse bankDeposit(final String name, final double amount) {
        return getBankController(controller -> resolve(controller.resolveBank(name))
                .map(bank -> transformTransaction(amount, bank.deposit(amount, getCurrency())))
                .orElseGet(() -> new EconomyResponse(0, 0, FAILURE, "Bank not found")));
    }

    @Override
    public EconomyResponse isBankOwner(final String name, final String playerName) {
        final var player = plugin.getServer().getOfflinePlayerIfCached(playerName);
        return isBankOwner(name, player);
    }

    @Override
    public EconomyResponse isBankOwner(final String name, @Nullable final OfflinePlayer player) {
        return getBankController(controller -> resolve(controller.resolveBank(name)).map(bank -> {
            final var response = player != null && bank.getOwner().equals(player.getUniqueId()) ? SUCCESS : FAILURE;
            return new EconomyResponse(0, bank.getBalance(getCurrency()).doubleValue(), response, null);
        }).orElseGet(() -> new EconomyResponse(0, 0, FAILURE, "Bank not found")));
    }

    @Override
    public EconomyResponse isBankMember(final String name, @Nullable final String playerName) {
        final var player = playerName != null ? plugin.getServer().getOfflinePlayerIfCached(playerName) : null;
        return isBankMember(name, player);
    }

    @Override
    public EconomyResponse isBankMember(final String name, @Nullable final OfflinePlayer player) {
        return getBankController(controller -> resolve(controller.resolveBank(name)).map(bank -> {
            final var response = player != null && bank.isMember(player.getUniqueId()) ? SUCCESS : FAILURE;
            return new EconomyResponse(0, bank.getBalance(getCurrency()).doubleValue(), response, null);
        }).orElseGet(() -> new EconomyResponse(0, 0, FAILURE, "Bank not found")));
    }

    @Override
    public List<String> getBanks() {
        if (bankController == null) return List.of();
        return bankController.getBanks().stream()
                .map(Bank::getName)
                .toList();
    }

    @Override
    public boolean createPlayerAccount(@Nullable final String playerName) {
        final var player = playerName != null ? plugin.getServer().getOfflinePlayerIfCached(playerName) : null;
        return player != null && createPlayerAccount(player);
    }

    @Override
    public boolean createPlayerAccount(@Nullable final OfflinePlayer player) {
        return createPlayerAccount(player, null);
    }

    @Override
    public boolean createPlayerAccount(@Nullable final String playerName, @Nullable final String worldName) {
        final var player = playerName != null ? plugin.getServer().getOfflinePlayerIfCached(playerName) : null;
        return player != null && createPlayerAccount(player, worldName);
    }

    @Override
    public boolean createPlayerAccount(@Nullable final OfflinePlayer player, @Nullable final String worldName) {
        return Optional.ofNullable(player).map(offline -> Optional.ofNullable(worldName)
                        .map(plugin.getServer()::getWorld)
                        .map(world -> tryResolve(economyController.createAccount(offline, world), null))
                        .orElseGet(() -> tryResolve(economyController.createAccount(offline), null)))
                .isPresent();
    }

    private Optional<Account> getAccount(@Nullable final OfflinePlayer player, @Nullable final String worldName) {
        return Optional.ofNullable(player).flatMap(offline -> Optional.ofNullable(worldName)
                .map(plugin.getServer()::getWorld)
                .flatMap(world -> tryResolve(economyController.resolveAccount(offline, world), Optional.empty()))
                .or(() -> tryResolve(economyController.resolveAccount(offline), Optional.empty())));
    }

    private EconomyResponse getBankController(final ThrowingFunction<EconomyResponse> function) {
        if (bankController == null) {
            return new EconomyResponse(0, 0, NOT_IMPLEMENTED, "Bank support not available");
        } else try {
            return function.apply(bankController);
        } catch (final Exception e) {
            plugin.getComponentLogger().error("Failed to get future synchronously", e);
            return new EconomyResponse(0, 0, FAILURE, e.getMessage());
        }
    }

    private EconomyResponse transformTransaction(final double amount, final TransactionResult result) {
        final var responseType = switch (result.status()) {
            case CURRENCY_NOT_SUPPORTED, FAILURE, INSUFFICIENT_FUNDS -> FAILURE;
            case SUCCESS -> SUCCESS;
        };
        final var errorMessage = switch (result.status()) {
            case CURRENCY_NOT_SUPPORTED -> "Currency not supported";
            case INSUFFICIENT_FUNDS -> "Insufficient funds";
            default -> null;
        };
        return new EconomyResponse(amount, result.balance().doubleValue(), responseType, errorMessage);
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
