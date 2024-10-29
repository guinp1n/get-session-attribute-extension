/*
 * Copyright 2018-present HiveMQ GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hivemq.extensions.helloworld;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.interceptor.publish.PublishInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishInboundInput;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishInboundOutput;
import com.hivemq.extension.sdk.api.packets.publish.ModifiablePublishPacket;
import com.hivemq.extension.sdk.api.services.EnterpriseServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.Optional;
/**
 *
 * @author Dasha Samkova
 * @since 4.33.1
 */
public class HelloWorldInterceptor implements PublishInboundInterceptor {

    private static final @NotNull Logger log = LoggerFactory.getLogger(HelloWorldInterceptor.class);

    public static String getValueAsStringFrom(ByteBuffer byteBuffer) {
        byteBuffer.rewind();
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    public void onInboundPublish(
            final @NotNull PublishInboundInput publishInboundInput,
            final @NotNull PublishInboundOutput publishInboundOutput) {

        final String clientId = publishInboundInput.getClientInformation().getClientId();
        final ModifiablePublishPacket publishPacket = publishInboundOutput.getPublishPacket();
        final String myAttributeName="my-session-attribute";

        final CompletableFuture<Optional<ByteBuffer>> myAttributeValueFuture = EnterpriseServices.sessionAttributeStore().get(clientId, myAttributeName);
        myAttributeValueFuture.whenComplete((aBuffer, throwable) -> {
            if (throwable == null) {
                if (aBuffer.isPresent()) {
                    final String myAttributeValueString=getValueAsStringFrom(aBuffer.get());

                    log.info("Inbound Publish from Client Id: {}. Attribute {} found. Value is: {}.",clientId,myAttributeName, myAttributeValueString);

                    publishPacket.getUserProperties().addUserProperty(myAttributeName, myAttributeValueString);

                } else {
                    log.info("In the inbound publish from client Id {}, the attribute {} is not found.",clientId,myAttributeName);
                }
            } else {
                log.warn("Exception with reason: {}", throwable.getMessage());
            }
        });
    }

}