package com.nisovin.shopkeepers.util.data.persistence.bukkit;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.nisovin.shopkeepers.util.data.ConfigBasedDataContainer;
import com.nisovin.shopkeepers.util.data.persistence.DataStore;
import com.nisovin.shopkeepers.util.data.persistence.DataStoreBase;
import com.nisovin.shopkeepers.util.data.persistence.InvalidDataFormatException;

/**
 * A {@link DataStore} that uses a Bukkit {@link FileConfiguration} to store, save, and load the data.
 */
public class BukkitConfigDataStore extends ConfigBasedDataContainer implements DataStoreBase {

	/**
	 * Creates a new {@link BukkitConfigDataStore} that uses the given Bukkit {@link FileConfiguration} to store, save,
	 * and load the data.
	 * 
	 * @param bukkitConfig
	 *            the Bukkit configuration, can be <code>null</code>
	 * @return the data store, or <code>null</code> if the given Bukkit configuration is <code>null</code>
	 */
	public static BukkitConfigDataStore of(FileConfiguration bukkitConfig) {
		if (bukkitConfig == null) return null;
		return new BukkitConfigDataStore(bukkitConfig);
	}

	/**
	 * Creates a new empty {@link BukkitConfigDataStore} based on a new {@link YamlConfiguration} as described by
	 * {@link #of(FileConfiguration)}.
	 * 
	 * @return the data store, not <code>null</code>
	 */
	public static BukkitConfigDataStore ofNewYamlConfig() {
		return of(new YamlConfiguration());
	}

	/////

	/**
	 * Creates a new {@link BukkitConfigDataStore}.
	 * 
	 * @param config
	 *            the Bukkit configuration, not <code>null</code>
	 */
	protected BukkitConfigDataStore(FileConfiguration config) {
		super(config);
	}

	@Override
	public FileConfiguration getConfig() {
		return (FileConfiguration) super.getConfig();
	}

	@Override
	public void loadFromString(String data) throws InvalidDataFormatException {
		try {
			this.getConfig().loadFromString(data);
		} catch (InvalidConfigurationException e) {
			throw new InvalidDataFormatException("Failed to load data as Bukkit config!", e);
		}
	}

	@Override
	public String saveToString() {
		return this.getConfig().saveToString();
	}
}
