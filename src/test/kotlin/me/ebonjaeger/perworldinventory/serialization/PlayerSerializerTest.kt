package me.ebonjaeger.perworldinventory.serialization

import com.dumptruckman.bukkit.configuration.util.SerializationHelper
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.nhaarman.mockito_kotlin.given
import me.ebonjaeger.perworldinventory.data.PlayerProfile
import net.minidev.json.JSONObject
import net.minidev.json.JSONStyle
import net.minidev.json.parser.JSONParser
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.UnsafeValues
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.inventory.ItemFactory
import org.bukkit.inventory.ItemStack
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.BDDMockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

/**
 * Tests for [PlayerSerializer].
 */
@PrepareForTest(Bukkit::class)
@RunWith(PowerMockRunner::class)
class PlayerSerializerTest
{

    private lateinit var unsafe: UnsafeValues

    @Before
    fun prepareTestingStuff()
    {
        PowerMockito.mockStatic(Bukkit::class.java)
        val itemFactory = PowerMockito.mock(ItemFactory::class.java)
        given(Bukkit.getItemFactory()).willReturn(itemFactory)
        val itemMeta = ItemMetaTestImpl()

        // No implementation of the ItemMeta interface readily available, so we return our own
        given(itemFactory.getItemMeta(ArgumentMatchers.any())).willAnswer { itemMeta }

        // Bukkit's serializer needs to know about our test implementation of ItemMeta and PlayerProfile or it will fail
        ConfigurationSerialization.registerClass(ItemMetaTestImpl::class.java)
        ConfigurationSerialization.registerClass(PlayerProfile::class.java)

        // As of 1.13, Bukkit has a compatibility layer, and serializing an item
        // now checks the data version to see what Material name to use.
        unsafe = PowerMockito.mock(UnsafeValues::class.java)
        BDDMockito.given(Bukkit.getUnsafe()).willReturn(unsafe)
        BDDMockito.given(unsafe.dataVersion).willReturn(1513)
        given(unsafe.getMaterial("AIR", 1513)).willReturn(Material.AIR)
        given(unsafe.getMaterial("DIAMOND", 1513)).willReturn(Material.DIAMOND)
        given(unsafe.getMaterial("DIAMOND_CHESTPLATE", 1513)).willReturn(Material.DIAMOND_CHESTPLATE)
        given(unsafe.getMaterial("IRON_LEGGINGS", 1513)).willReturn(Material.IRON_LEGGINGS)
        given(unsafe.getMaterial("GOLDEN_APPLE", 1513)).willReturn(Material.GOLDEN_APPLE)
    }

    @Test @Suppress("UNCHECKED_CAST")
    fun verifyCorrectSerialization()
    {
        // given
        val armor = arrayOf(ItemStack(Material.AIR), ItemStack(Material.DIAMOND_CHESTPLATE),
                ItemStack(Material.IRON_LEGGINGS), ItemStack(Material.AIR))
        val enderChest = arrayOf(ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND))
        val inventory = arrayOf(ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND))
        val profile = PlayerProfile(armor, enderChest, inventory, false, "Bob",
                5.0F, 50.5F, false, 20, 20.0, 14.0, GameMode.SURVIVAL, 5, 4.86F,
                mutableListOf(), 0.0F, 0, 500, 500, 0.0)

        // when
        val map = SerializationHelper.serialize(profile) as Map<String, *>
        val json = JSONObject(map).toJSONString(JSONStyle.LT_COMPRESS)

        // then
        val parsed = JSONParser(JSONParser.USE_INTEGER_STORAGE).parse(json) as JSONObject
        val result = SerializationHelper.deserialize(parsed) as PlayerProfile
        assertProfilesAreEqual(profile, result)
    }

    private fun assertProfilesAreEqual(expected: PlayerProfile, actual: PlayerProfile)
    {
        for (i in 0 until expected.armor.size)
        {
            assertThat(expected.armor[i].type, equalTo(actual.armor[i].type))
        }

        for (i in 0 until expected.inventory.size)
        {
            assertThat(expected.inventory[i].type, equalTo(actual.inventory[i].type))
        }

        for (i in 0 until expected.enderChest.size)
        {
            assertThat(expected.enderChest[i].type, equalTo(actual.enderChest[i].type))
        }

        assertThat(expected.allowFlight, equalTo(actual.allowFlight))
        assertThat(expected.displayName, equalTo(actual.displayName))
        assertThat(expected.exhaustion, equalTo(actual.exhaustion))
        assertThat(expected.experience, equalTo(actual.experience))
        assertThat(expected.isFlying, equalTo(actual.isFlying))
        assertThat(expected.allowFlight, equalTo(actual.allowFlight))
        assertThat(expected.foodLevel, equalTo(actual.foodLevel))
        assertThat(expected.maxHealth, equalTo(actual.maxHealth))
        assertThat(expected.health, equalTo(actual.health))
        assertThat(expected.gameMode, equalTo(actual.gameMode))
        assertThat(expected.level, equalTo(actual.level))
        assertThat(expected.saturation, equalTo(actual.saturation))
        assertThat(expected.potionEffects, equalTo(actual.potionEffects))
        assertThat(expected.fallDistance, equalTo(actual.fallDistance))
        assertThat(expected.fireTicks, equalTo(actual.fireTicks))
        assertThat(expected.maximumAir, equalTo(actual.maximumAir))
        assertThat(expected.remainingAir, equalTo(actual.remainingAir))
        assertThat(expected.balance, equalTo(actual.balance))
    }
}
