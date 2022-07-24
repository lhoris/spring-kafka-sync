package com.lhoris.msa.account.common.util.module;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    private static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String SLASH_FORMAT = "yyyy/MM/dd HH:mm:ss";

    @Override
    public LocalDateTime deserialize(JsonParser jsonParser,
                                     DeserializationContext deserializationContext) throws IOException, JacksonException {
        // OBJECT
        if (JsonToken.START_OBJECT == jsonParser.currentToken()) {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            JsonNode dateNode = node.get("date");
            JsonNode timeNode = node.get("time");


            if (dateNode != null && timeNode != null) { // GSON TYPE
                return this.parseFromGsonObject(node);
            } else { // JACKSON TYPE
                return this.parseFromJacksonObject(node);
            }

        }

        // STRING
        if (JsonToken.VALUE_STRING == jsonParser.currentToken()) {

            String value = jsonParser.getText();
            if (!value.contains("T") && !value.contains("/")) { // T가 없음
                return LocalDateTime.parse(value, new DateTimeFormatterBuilder()
                        .appendPattern(DEFAULT_FORMAT)
                        .appendFraction(ChronoField.MILLI_OF_SECOND, 0, 9, true)
                        .toFormatter()
                );
            }
            if (value.contains("T")) { // T를 가지고 있음
                return LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
            if (value.contains("/")) { // SLASH 구분자 패턴
                return LocalDateTime.parse(value, new DateTimeFormatterBuilder()
                        .appendPattern(SLASH_FORMAT)
                        .appendFraction(ChronoField.MILLI_OF_SECOND, 0, 9, true)
                        .toFormatter()
                );
            }
        }

        return null;
    }

    private LocalDateTime parseFromGsonObject(JsonNode node) {
        JsonNode dateNode = node.get("date");
        JsonNode timeNode = node.get("time");
        LocalDate localDate = LocalDate.of(dateNode.get("year").asInt(), dateNode.get("month").asInt(), dateNode.get("day").asInt());
        LocalTime localTime = LocalTime.of(timeNode.get("hour").asInt(), timeNode.get("minute").asInt(), timeNode.get("second").asInt(), timeNode.get("nano") != null ? timeNode.get("nano").asInt() : 000);
        return LocalDateTime.of(localDate, localTime);
    }

    private LocalDateTime parseFromJacksonObject(JsonNode node) {
        LocalDate localDate = LocalDate.of(node.get("year").asInt(), node.get("month").asInt(), node.get("dayOfMonth").asInt());
        LocalTime localTime = LocalTime.of(node.get("hour").asInt(), node.get("minute").asInt(), node.get("second").asInt(), node.get("nano") != null ? node.get("nano").asInt() : 000);
        return LocalDateTime.of(localDate, localTime);
    }


}
