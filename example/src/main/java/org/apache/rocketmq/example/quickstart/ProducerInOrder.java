/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.rocketmq.example.quickstart;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.example.entity.Order;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This class demonstrates how to send messages to brokers using provided {@link DefaultMQProducer}.
 */
public class ProducerInOrder {
    public static void main(String[] args) throws MQClientException, InterruptedException, RemotingException, MQBrokerException {

        /*
         * Instantiate with a producer group name.
         */
        DefaultMQProducer producer = new DefaultMQProducer("please_rename_unique_group_name");

        producer.setNamesrvAddr("127.0.0.1:9876");
        /*
         * Specify name server addresses.
         * <p/>
         *
         * Alternatively, you may specify name server addresses via exporting environmental variable: NAMESRV_ADDR
         * <pre>
         * {@code
         * producer.setNamesrvAddr("name-server1-ip:9876;name-server2-ip:9876");
         * }
         * </pre>
         */

        /*
         * Launch the instance.
         */
        producer.start();

        List<Order> orderList=new ProducerInOrder().buildOrders();

        for(int i=0;i<orderList.size();i++){
            String body=orderList.get(i).toString();
            Message msg = new Message("TopicTest" /* Topic */,
                    "TagA" /* Tag */,
                    body.getBytes() /* Message body */);
            SendResult sendResult = producer.send(msg, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                    Integer id= (Integer) arg;
                    int index=id % mqs.size();
                   return mqs.get(index);
                }
            },orderList.get(i).getId());

            System.out.println(String.format("SendResult status:%s,queueId:%d,body:%s",sendResult.getSendStatus(),sendResult.getMessageQueue().getQueueId(),body));
        }

        /*
         * Shut down once the producer instance is not longer in use.
         */
        TimeUnit.SECONDS.sleep(10);
        producer.shutdown();
    }

    private List<Order> buildOrders(){
        List<Order> orderList=new ArrayList<Order>();
        Order orderDemo1=new Order();
        orderDemo1.setId(1);
        orderDemo1.setDesc("创建");
        orderList.add(orderDemo1);

        Order orderDemo2=new Order();
        orderDemo2.setId(1);
        orderDemo2.setDesc("支付");
        orderList.add(orderDemo2);

        Order orderDemo5=new Order();
        orderDemo5.setId(1);
        orderDemo5.setDesc("完成");
        orderList.add(orderDemo5);


        Order orderDemo3=new Order();
        orderDemo3.setId(2);
        orderDemo3.setDesc("创建");
        orderList.add(orderDemo3);

        Order orderDemo4=new Order();
        orderDemo4.setId(2);
        orderDemo4.setDesc("支付");
        orderList.add(orderDemo4);

        Order orderDemo6=new Order();
        orderDemo6.setId(2);
        orderDemo6.setDesc("完成");
        orderList.add(orderDemo6);




      /*  orderDemo=new Order();
        orderDemo.setId(1);
        orderDemo.setDesc("完成");
        orderList.add(orderDemo);*/

/*        orderDemo.setId(2);
        orderDemo.setDesc("创建");
        orderList.add(orderDemo);

        orderDemo=new Order();
        orderDemo.setId(2);
        orderDemo.setDesc("支付");
        orderList.add(orderDemo);*/

       /* orderDemo=new Order();
        orderDemo.setId(2);
        orderDemo.setDesc("完成");
        orderList.add(orderDemo);*/
        return orderList;
    }
}
