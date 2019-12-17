package com.stevesoltys.seedvault.metadata

import com.stevesoltys.seedvault.crypto.Crypto
import com.stevesoltys.seedvault.getRandomString
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import kotlin.random.Random

@TestInstance(PER_CLASS)
internal class MetadataWriterDecoderTest {

    private val crypto = mockk<Crypto>()

    private val encoder = MetadataWriterImpl(crypto)
    private val decoder = MetadataReaderImpl(crypto)

    @Test
    fun `encoded metadata matches decoded metadata (no packages)`() {
        val metadata = getMetadata()
        assertEquals(metadata, decoder.decode(encoder.encode(metadata), metadata.version, metadata.token))
    }

    @Test
    fun `encoded metadata matches decoded metadata (with package, no apk info)`() {
        val time = Random.nextLong()
        val packages = HashMap<String, PackageMetadata>().apply {
            put(getRandomString(), PackageMetadata(time))
        }
        val metadata = getMetadata(packages)
        assertEquals(metadata, decoder.decode(encoder.encode(metadata), metadata.version, metadata.token))
    }

    @Test
    fun `encoded metadata matches decoded metadata (full package)`() {
        val packages = HashMap<String, PackageMetadata>().apply {
            put(getRandomString(), PackageMetadata(
                    time = Random.nextLong(),
                    version = Random.nextLong(),
                    installer = getRandomString(),
                    signatures = listOf(getRandomString(), getRandomString())))
        }
        val metadata = getMetadata(packages)
        assertEquals(metadata, decoder.decode(encoder.encode(metadata), metadata.version, metadata.token))
    }

    @Test
    fun `encoded metadata matches decoded metadata (two full packages)`() {
        val packages = HashMap<String, PackageMetadata>().apply {
            put(getRandomString(), PackageMetadata(
                    time = Random.nextLong(),
                    version = Random.nextLong(),
                    installer = getRandomString(),
                    signatures = listOf(getRandomString())
            ))
            put(getRandomString(), PackageMetadata(
                    time = Random.nextLong(),
                    version = Random.nextLong(),
                    installer = getRandomString(),
                    signatures = listOf(getRandomString(), getRandomString())
            ))
        }
        val metadata = getMetadata(packages)
        assertEquals(metadata, decoder.decode(encoder.encode(metadata), metadata.version, metadata.token))
    }

    private fun getMetadata(packageMetadata: Map<String, PackageMetadata> = HashMap()): BackupMetadata {
        return BackupMetadata(
                version = Random.nextBytes(1)[0],
                token = Random.nextLong(),
                time = Random.nextLong(),
                androidVersion = Random.nextInt(),
                androidIncremental = getRandomString(),
                deviceName = getRandomString(),
                packageMetadata = packageMetadata
        )
    }

}
