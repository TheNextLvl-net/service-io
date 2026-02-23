package net.thenextlvl.service.wrapper;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.thenextlvl.service.api.economy.Account;
import net.thenextlvl.service.api.economy.EconomyController;
import net.thenextlvl.service.api.economy.bank.Bank;
import net.thenextlvl.service.api.economy.bank.BankController;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static net.milkbowl.vault.economy.EconomyResponse.ResponseType.FAILURE;
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

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return economyController.getName() + " Wrapper";
    }

    private BankController bankController() throws UnsupportedOperationException {
        if (bankController != null) return bankController;
        throw new UnsupportedOperationException(getName() + " has no bank support");
    }

    @Override
    public boolean hasBankSupport() {
        return bankController != null;
    }

    @Override
    public int fractionalDigits() {
        return economyController.fractionalDigits();
    }

    @Override
    public String format(final double amount) {
        return economyController.format(amount);
    }

    @Override
    public String currencyNamePlural() {
        return economyController.getCurrencyNamePlural(Locale.US);
    }

    @Override
    public String currencyNameSingular() {
        return economyController.getCurrencyNameSingular(Locale.US);
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
                .map(Account::getBalance)
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
        return getAccount(player, worldName).map(account -> {
            final var balance = account.getBalance();
            final var withdraw = account.withdraw(amount);
            final var responseType = amount != 0 && balance.equals(withdraw) ? FAILURE : SUCCESS;
            return new EconomyResponse(amount, withdraw.doubleValue(), responseType, null);
        }).orElseGet(() -> new EconomyResponse(amount, 0, FAILURE, null));
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
        return getAccount(player, worldName).map(account -> {
            final var balance = account.getBalance();
            final var deposit = account.deposit(amount);
            final var responseType = amount != 0 && balance.equals(deposit) ? FAILURE : SUCCESS;
            return new EconomyResponse(amount, deposit.doubleValue(), responseType, null);
        }).orElseGet(() -> new EconomyResponse(amount, 0, FAILURE, null));
    }

    @Override
    public EconomyResponse createBank(final String name, @Nullable final String playerName) {
        final var player = playerName != null ? plugin.getServer().getOfflinePlayerIfCached(playerName) : null;
        return createBank(name, player);
    }

    @Override
    public EconomyResponse createBank(final String name, @Nullable final OfflinePlayer player) {
        return Optional.ofNullable(player)
                .map(offline -> bankController().createBank(offline, name).join())
                .map(bank -> new EconomyResponse(0, 0, SUCCESS, null))
                .orElseGet(() -> new EconomyResponse(0, 0, FAILURE, null));
    }

    @Override
    public EconomyResponse deleteBank(final String name) {
        final var deleted = bankController().deleteBank(name).join();
        return new EconomyResponse(0, 0, deleted ? SUCCESS : FAILURE, null);
    }

    @Override
    public EconomyResponse bankBalance(final String name) {
        try {
            final var bank = bankController().tryGetBank(name).join();
            return new EconomyResponse(0, bank.getBalance().doubleValue(), SUCCESS, null);
        } catch (final Exception e) {
            return new EconomyResponse(0, 0, FAILURE, e.getMessage());
        }
    }

    @Override
    public EconomyResponse bankHas(final String name, final double amount) {
        try {
            final var bank = bankController().tryGetBank(name).join();
            final var balance = bank.getBalance().doubleValue();
            final var response = balance >= amount ? SUCCESS : FAILURE;
            return new EconomyResponse(amount, balance, response, null);
        } catch (final Exception e) {
            return new EconomyResponse(0, 0, FAILURE, e.getMessage());
        }
    }

    @Override
    public EconomyResponse bankWithdraw(final String name, final double amount) {
        try {
            final var bank = bankController().tryGetBank(name).join();
            final var balance = bank.withdraw(amount).doubleValue();
            final var response = balance >= amount ? SUCCESS : FAILURE;
            return new EconomyResponse(amount, balance, response, null);
        } catch (final Exception e) {
            return new EconomyResponse(0, 0, FAILURE, e.getMessage());
        }
    }

    @Override
    public EconomyResponse bankDeposit(final String name, final double amount) {
        try {
            final var bank = bankController().tryGetBank(name).join();
            final var balance = bank.deposit(amount).doubleValue();
            final var response = balance >= amount ? SUCCESS : FAILURE;
            return new EconomyResponse(amount, balance, response, null);
        } catch (final Exception e) {
            return new EconomyResponse(0, 0, FAILURE, e.getMessage());
        }
    }

    @Override
    public EconomyResponse isBankOwner(final String name, final String playerName) {
        final var player = plugin.getServer().getOfflinePlayerIfCached(playerName);
        return isBankOwner(name, player);
    }

    @Override
    public EconomyResponse isBankOwner(final String name, @Nullable final OfflinePlayer player) {
        try {
            final var bank = bankController().tryGetBank(name).join();
            final var response = player != null && bank.getOwner().equals(player.getUniqueId()) ? SUCCESS : FAILURE;
            return new EconomyResponse(0, bank.getBalance().doubleValue(), response, null);
        } catch (final Exception e) {
            return new EconomyResponse(0, 0, FAILURE, e.getMessage());
        }
    }

    @Override
    public EconomyResponse isBankMember(final String name, @Nullable final String playerName) {
        final var player = playerName != null ? plugin.getServer().getOfflinePlayerIfCached(playerName) : null;
        return isBankMember(name, player);
    }

    @Override
    public EconomyResponse isBankMember(final String name, @Nullable final OfflinePlayer player) {
        try {
            final var bank = bankController().tryGetBank(name).join();
            final var response = player != null && bank.isMember(player.getUniqueId()) ? SUCCESS : FAILURE;
            return new EconomyResponse(0, bank.getBalance().doubleValue(), response, null);
        } catch (final Exception e) {
            return new EconomyResponse(0, 0, FAILURE, e.getMessage());
        }
    }

    @Override
    public List<String> getBanks() {
        return bankController().getBanks().stream()
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
                        .map(world -> economyController.createAccount(offline, world).join())
                        .orElseGet(() -> economyController.createAccount(offline).join()))
                .isPresent();
    }

    private Optional<Account> getAccount(@Nullable final OfflinePlayer player, @Nullable final String worldName) {
        return Optional.ofNullable(player).flatMap(offline -> Optional.ofNullable(worldName)
                .map(plugin.getServer()::getWorld)
                .flatMap(world -> economyController.tryGetAccount(offline, world).join())
                .or(() -> economyController.tryGetAccount(offline).join()));
    }
}
