package kr.sobin.fakeplayer

import org.bukkit.Location
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import java.util.*
import net.minecraft.network.protocol.game.*
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.GameType
import net.minecraft.network.chat.Component
import com.mojang.authlib.GameProfile
import net.minecraft.server.level.ServerLevel
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import com.mojang.authlib.properties.Property
import java.lang.reflect.Field
import java.net.URL
import java.io.InputStreamReader
import javax.net.ssl.HttpsURLConnection
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser


class NMSFakePlayer(
    name: String,
    location: Location,
    skin: String? = null
) : AbstractFakePlayer(name, UUID.randomUUID(), location) {

    private val entityId = uuid.hashCode() and Int.MAX_VALUE
    private val gameProfile = GameProfile(uuid, name).apply {
        // 스킨이 지정되지 않았다면 닉네임으로 스킨 시도
        if (skin == null) {
            val uuidFromName = fetchUUIDFromName(name)
            if (uuidFromName != null) {
                val (value, signature) = fetchSkinFromMojang(uuidFromName)
                if (value != null && signature != null) {
                    properties.put("textures", Property("textures", value, signature))
                }
            }
        } else {
            val (value, signature) = when {
                skin.length > 32 -> Pair(skin, null)
                skin.length in 32..36 -> fetchSkinFromMojang(skin)
                else -> {
                    val uuid = fetchUUIDFromName(skin)
                    if (uuid != null) fetchSkinFromMojang(uuid) else Pair(null, null)
                }
            }
            if (value != null) {
                if (signature != null)
                    properties.put("textures", Property("textures", value, signature))
                else
                    properties.put("textures", Property("textures", value))
            }
        }
    }

    // 패킷 필드 클래스
    private class PacketField {
        class UUID {
            companion object {
                const val FIELD = "b"
            }
        }
        class EntityId {
            companion object {
                const val FIELD = "a"
            }
        }
        class Position {
            companion object {
                const val X = "c"
                const val Y = "d"
                const val Z = "e"
            }
        }
        class Rotation {
            companion object {
                const val YAW = "f"
                const val PITCH = "g"
            }
        }
    }

    private fun setFieldValue(obj: Any, fieldName: String, value: Any?) {
        val field = obj.javaClass.getDeclaredField(fieldName)
        field.isAccessible = true
        when (value) {
            is Int -> field.setInt(obj, value)
            is Byte -> field.setByte(obj, value)
            is Double -> field.setDouble(obj, value)
            else -> field.set(obj, value)
        }
    }

    override fun spawn(to: Player) {
        try {
            val connection = (to as CraftPlayer).handle.connection
            val nmsPlayer = to.handle

            val updatePacket = ClientboundPlayerInfoUpdatePacket(
                EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER),
                Collections.singletonList(createPlayerInfo(nmsPlayer))
            )
            connection.send(updatePacket)

            val spawnPacket = ClientboundAddPlayerPacket(nmsPlayer)
            setFieldValue(spawnPacket, PacketField.UUID.FIELD, uuid)
            setFieldValue(spawnPacket, PacketField.EntityId.FIELD, entityId)
            setFieldValue(spawnPacket, PacketField.Position.X, location.x)
            setFieldValue(spawnPacket, PacketField.Position.Y, location.y)
            setFieldValue(spawnPacket, PacketField.Position.Z, location.z)
            setFieldValue(spawnPacket, PacketField.Rotation.YAW, (location.yaw * 256.0f / 360.0f).toInt().toByte())
            setFieldValue(spawnPacket, PacketField.Rotation.PITCH, (location.pitch * 256.0f / 360.0f).toInt().toByte())

            connection.send(spawnPacket)

            // 모든 스킨 레이어 활성화
            // dataManager 생성 및 값 정의
            val skinBits: Byte = 0x7f
            val skinPartsAccessor = EntityDataAccessor(17, EntityDataSerializers.BYTE)
            val dataList = listOf(
                SynchedEntityData.DataValue(17, EntityDataSerializers.BYTE, skinBits)
            )
            val dataPacket = ClientboundSetEntityDataPacket(entityId, dataList)
            connection.send(dataPacket)

        } catch (e: Exception) {
            e.printStackTrace()
            to.sendMessage("§c[FakePlayer] Failed to spawn: ${e.message}")
        }
    }

    override fun destroy(to: Player) {
        try {
            val connection = (to as CraftPlayer).handle.connection
            connection.send(ClientboundRemoveEntitiesPacket(entityId))
            connection.send(ClientboundPlayerInfoRemovePacket(Collections.singletonList(uuid)))
        } catch (e: Exception) {
            e.printStackTrace()
            to.sendMessage("§c[FakePlayer] Failed to remove: ${e.message}")
        }
    }

    private fun createPlayerInfo(nmsPlayer: ServerPlayer): ClientboundPlayerInfoUpdatePacket.Entry {
        return ClientboundPlayerInfoUpdatePacket.Entry(
            uuid,
            gameProfile,
            true,
            0,
            GameType.SURVIVAL,
            Component.literal(name),
            null
        )
    }

    private fun fetchSkinFromMojang(uuid: String): Pair<String?, String?> {
        return try {
            val url = URL("https://sessionserver.mojang.com/session/minecraft/profile/$uuid?unsigned=false")
            val conn = url.openConnection() as HttpsURLConnection
            conn.connectTimeout = 3000
            conn.readTimeout = 3000
            conn.requestMethod = "GET"
            conn.doInput = true
            conn.connect()
            val reader = InputStreamReader(conn.inputStream)
            val json = JSONParser().parse(reader) as JSONObject
            val props = (json["properties"] as org.json.simple.JSONArray)[0] as JSONObject
            val value = props["value"] as? String
            val sig = props["signature"] as? String
            reader.close()
            conn.disconnect()
            Pair(value, sig)
        } catch (e: Exception) {
            e.printStackTrace()
            Pair(null, null)
        }
    }

    private fun fetchUUIDFromName(name: String): String? {
        return try {
            val url = URL("https://api.mojang.com/users/profiles/minecraft/$name")
            val conn = url.openConnection() as HttpsURLConnection
            conn.connectTimeout = 3000
            conn.readTimeout = 3000
            conn.requestMethod = "GET"
            conn.doInput = true
            conn.connect()
            val reader = InputStreamReader(conn.inputStream)
            val json = JSONParser().parse(reader) as JSONObject
            val uuid = json["id"] as? String
            reader.close()
            conn.disconnect()
            uuid
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}