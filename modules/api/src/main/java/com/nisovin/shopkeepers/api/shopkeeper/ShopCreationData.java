package com.nisovin.shopkeepers.api.shopkeeper;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import com.nisovin.shopkeepers.api.shopobjects.ShopObjectType;
import com.nisovin.shopkeepers.api.shopobjects.virtual.VirtualShopObjectType;

/**
 * Holds the different possible arguments needed for the creation of a shopkeeper of a certain type.
 * <p>
 * Additional data might be available through sub-classes, or when dynamically added via
 * {@link #setValue(String, Object)}.
 */
public abstract class ShopCreationData {

	private final Player creator; // can be null
	private final ShopType<?> shopType; // not null
	private final ShopObjectType<?> shopObjectType; // not null
	private Location spawnLocation; // modifiable, can be null for virtual shops
	private BlockFace targetedBlockFace; // can be null, modifiable

	private Map<String, Object> additionalData;

	/**
	 * Creates a {@link ShopCreationData}.
	 * 
	 * @param creator
	 *            the creator, can be <code>null</code>
	 * @param shopType
	 *            the shop type, not <code>null</code>
	 * @param shopObjectType
	 *            the shop object type, not <code>null</code>
	 * @param spawnLocation
	 *            the spawn location, can be <code>null</code> for virtual shops
	 * @param targetedBlockFace
	 *            the targeted block face, can be <code>null</code>
	 */
	protected ShopCreationData(	Player creator, ShopType<?> shopType, ShopObjectType<?> shopObjectType,
								Location spawnLocation, BlockFace targetedBlockFace) {
		Validate.notNull(shopType, "Shop type is null!");
		Validate.notNull(shopObjectType, "Shop object type is null!");
		this.creator = creator;
		this.shopType = shopType;
		this.shopObjectType = shopObjectType;
		if (spawnLocation != null) {
			Validate.notNull(spawnLocation.getWorld(), "Spawn location is missing world!");
			spawnLocation.checkFinite();
			this.spawnLocation = spawnLocation.clone();
		} else {
			Validate.isTrue(shopObjectType instanceof VirtualShopObjectType, "Spawn location is null, but the shop object type is not virtual!");
			this.spawnLocation = null;
		}
		this.targetedBlockFace = targetedBlockFace;
	}

	/**
	 * The creator of the shop.
	 * 
	 * @return the creating player, might be <code>null</code> (depending on which type of shopkeeper is created and in
	 *         which context)
	 */
	public Player getCreator() {
		return creator;
	}

	/**
	 * The type of shop to create.
	 * 
	 * @return the shop type, not <code>null</code>
	 */
	public ShopType<?> getShopType() {
		return shopType;
	}

	/**
	 * The object type for the shop.
	 * 
	 * @return the shop object type, not <code>null</code>
	 */
	public ShopObjectType<?> getShopObjectType() {
		return shopObjectType;
	}

	/**
	 * The location the shopkeeper gets created at.
	 * 
	 * @return the spawn location, can be <code>null</code> for virtual shops
	 */
	public Location getSpawnLocation() {
		return (spawnLocation == null) ? null : spawnLocation.clone();
	}

	/**
	 * Sets the spawn location.
	 * <p>
	 * Has to be located in the same world as the previous spawn location.
	 * 
	 * @param newSpawnLocation
	 *            the new spawn location
	 */
	public void setSpawnLocation(Location newSpawnLocation) {
		if (!(shopObjectType instanceof VirtualShopObjectType)) {
			Validate.notNull(newSpawnLocation, "New spawn location is null, but the shop object type is not virtual!");
		}
		if (newSpawnLocation == null) {
			this.spawnLocation = null;
		} else {
			Validate.notNull(newSpawnLocation.getWorld(), "New spawn location is missing world!");
			newSpawnLocation.checkFinite();
			if (this.spawnLocation != null) {
				Validate.isTrue(this.spawnLocation.getWorld() == newSpawnLocation.getWorld(),
						"Cannot set the spawn location to another world!");
			}
			this.spawnLocation = newSpawnLocation.clone();
		}
	}

	/**
	 * The block face clicked or targeted during shop creation.
	 * 
	 * @return the targeted block face, can be <code>null</code>
	 */
	public BlockFace getTargetedBlockFace() {
		return targetedBlockFace;
	}

	/**
	 * Sets the targeted block face.
	 * 
	 * @param blockFace
	 *            the new block face
	 */
	public void setTargetedBlockFace(BlockFace blockFace) {
		this.targetedBlockFace = blockFace;
	}

	/**
	 * Gets a previously set value for the specific key.
	 * 
	 * @param <T>
	 *            the type of the value
	 * @param key
	 *            the key
	 * @return the value, or <code>null</code> if there is no value for the specified key
	 */
	@SuppressWarnings("unchecked")
	public <T> T getValue(String key) {
		if (additionalData == null) return null;
		return (T) additionalData.get(key);
	}

	/**
	 * Sets a value for the specified key.
	 * 
	 * @param <T>
	 *            the type of the value
	 * @param key
	 *            the key
	 * @param value
	 *            the value, or <code>null</code> to remove the value for the specified key
	 */
	public <T> void setValue(String key, T value) {
		if (value == null) {
			if (additionalData == null) return;
			additionalData.remove(key);
		} else {
			if (additionalData == null) {
				additionalData = new HashMap<>();
			}
			additionalData.put(key, value);
		}
	}
}
