package com.flink;
import java.util.Properties;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer082;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;
import org.apache.flink.util.Collector;

public class streamingkafka {

    public static void main(String args[]) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("zookeeper.connect", "localhost:2181");
        props.put("group.id", "group_test");

        DataStream<String> messageStream = env
                .addSource(new FlinkKafkaConsumer082<>("test", new SimpleStringSchema(), props));

        DataStream<Tuple2<String, Integer>> dataStream2 = messageStream.flatMap(new streamingkafka.Splitter())
                .groupBy(0)
                .sum(1);
        dataStream2.print();
        
        /*
         DataStream<String> messageStream = env
                .addSource(new FlinkKafkaConsumer082<>("test", new SimpleStringSchema(), props));

        messageStream.rebalance().map(new MapFunction<String, String>() {
            private static final long serialVersionUID = -6867736771747690202L;

            @Override
            public String map(String value) throws Exception {
                //long time = System.currentTimeMillis();
                return value;
            }
        }).print();
        */

        env.execute();
    }

    public static class Splitter implements FlatMapFunction<String, Tuple2<String, Integer>> {

        @Override
        public void flatMap(String sentence, Collector<Tuple2<String, Integer>> out) throws Exception {
            for (String word : sentence.split(" ")) {
                out.collect(new Tuple2<String, Integer>(word, 1));
            }
        }

    }

}

