package persistence

import models.Guest
import models.Reservation
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.lang.Exception

class YAMLSerializer(private val file: File) : Serializer {
    private val yaml = Yaml()

    @Throws(Exception::class)
    override fun read(): Any {
        FileReader(file).use { reader ->
            return yaml.load(reader)
        }
    }

    @Throws(Exception::class)
    override fun write(obj: Any?) {
        FileWriter(file).use { writer ->
            yaml.dump(obj, writer)
        }
    }
}
