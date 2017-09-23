package singleprograms;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.FileReader;
import org.apache.avro.generic.GenericContainer;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonEncoder;
import org.apache.avro.mapred.FsInput;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class AvroToJson {
    public final static void main(final String[] args) throws IOException, InterruptedException {

        if (args.length == 0) {
            System.err.format("Usage: %s scheme://host:port/path/to/file.avro [count]\n", AvroToJson.class.getSimpleName());
        } else {
            int count = args.length == 2 ? Integer.parseInt(args[1]) : -1;
            final GenericDatumReader<Object> reader = new GenericDatumReader<Object>();
            final Path path = new Path(args[0]);
            final FsInput input = new FsInput(path, new Configuration());
            final FileReader<Object> fileReader = DataFileReader.openReader(input, reader);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                final Schema schema = fileReader.getSchema();
                final DatumWriter<Object> writer = new GenericDatumWriter<Object>(schema);
                final JsonEncoder encoder = EncoderFactory.get().jsonEncoder(schema, os);
                for (final Object datum : fileReader) {
                    writer.write(datum, encoder);
                    encoder.flush();
                    String jsonString = os.toString();
                    System.out.print(jsonString);
                    os.reset();
                    if (--count == 0) {
                        break;
                    }
                }
                os.close();

            } finally {
                fileReader.close();
            }
        }
    }
}