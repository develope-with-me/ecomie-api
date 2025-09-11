package org.csbf.security.utils.commons;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.csbf.security.exceptions.Problem;
import org.csbf.security.exceptions.Problems;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
@Component
public interface Mapper<T extends Domain, E extends Entity> {
    T asDomainObject(E entity);

    E asEntity(T domain);

    List<T> asDomainObjects(List<E> entities);

    List<E> asEntities(List<T> domainObjs);

    ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    static <O> O fromJsonString(String jsonString, Class<O> clazz)  {
        if (jsonString == null || clazz == null) {
            throw Problems.INVALID_PARAMETER_ERROR.appendDetail("Parameters jsonString and clazz must not be null").toException();
        }
        try {
            return OBJECT_MAPPER.readValue(jsonString, clazz);
        } catch (JsonProcessingException e) {
            throw  Problems.JSON_DESERIALIZATION_ERROR.appendDetail(e.toString()).toException();
        }
    }

    static String toJsonString(Object value) {
        if (value == null) {
            throw Problems.INVALID_PARAMETER_ERROR.appendDetail("Parameter value must not be null").toException();
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
         } catch (JsonProcessingException e) {
            throw  Problems.JSON_SERIALIZATION_ERROR.appendDetail(e.toString()).toException();
         }
    }

    static <O> O fromJsonObject(JSONObject jsonObject, Class<O> clazz)  {
        String jsonString = toJsonString(jsonObject);
        return fromJsonString(jsonString, clazz);
    }


    static JSONObject toJsonObject(Object value) {
        if (value == null) {
            throw Problems.INVALID_PARAMETER_ERROR.appendDetail("Parameter value must not be null").toException();
        }
        try {
            // Convert the object to a JSON string
            String jsonString = OBJECT_MAPPER.writeValueAsString(value);

            // Create a JSONObject from the JSON string
            return (JSONObject) new JSONParser().parse(jsonString);
//            return (JSONObject) JSONObject.stringToValue(OBJECT_MAPPER.writeValueAsString(value)); // org.json.JSONObject equivalent of  the last two lines of org.json.simple.JSONObject
         } catch (JsonProcessingException | ParseException e) {
            throw  Problems.JSON_SERIALIZATION_ERROR.appendDetail(e.toString()).toException();
         }
    }

    static <O> List<O> fromJsonArray(String jsonArrayString, Class<O> clazz) {
        if (jsonArrayString == null || clazz == null) {
            throw Problems.INVALID_PARAMETER_ERROR.appendDetail("Parameters jsonArrayString and clazz must not be null").toException();
        }
        try {
            return OBJECT_MAPPER.readValue(jsonArrayString,
                    OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (JsonProcessingException e) {
            throw  Problems.JSON_DESERIALIZATION_ERROR.appendDetail(e.toString()).toException();
        }
    }

    static <O> List<O> fromJsonArray(Collection<String> jsonArray, Class<O> clazz) {
        if (jsonArray == null || clazz == null) {
            throw Problems.INVALID_PARAMETER_ERROR.appendDetail("Parameters jsonArray and clazz must not be null").toException();
        }
        try {
            return OBJECT_MAPPER.readValue(OBJECT_MAPPER.writeValueAsString(jsonArray),
                    OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (JsonProcessingException e) {
            throw  Problems.JSON_DESERIALIZATION_ERROR.appendDetail(e.toString()).toException();
        }
    }

    static <O> List<O> fromJsonArray(Object jsonArray, Class<O> clazz) {
        if (jsonArray == null || clazz == null) {
            throw new IllegalArgumentException("Parameters jsonArray and clazz must not be null");
        }
        try {
            return OBJECT_MAPPER.readValue(OBJECT_MAPPER.writeValueAsString(jsonArray),
                    OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (JsonProcessingException e) {
            throw  Problems.JSON_DESERIALIZATION_ERROR.appendDetail(e.toString()).toException();
        }
    }

    static <O> Collection<O> toJsonArray(List<O> list) {
        if (list == null) {
            throw Problems.INVALID_PARAMETER_ERROR.appendDetail("Parameter list must not be null").toException();
        }
        try {
            return OBJECT_MAPPER.readValue(OBJECT_MAPPER.writeValueAsString(list),
                    OBJECT_MAPPER.getTypeFactory().constructCollectionType(Collection.class, Object.class));
        } catch (JsonProcessingException e) {
            throw  Problems.JSON_DESERIALIZATION_ERROR.appendDetail(e.toString()).toException();
        }
    }

    default T toDomainObject(E entity) {
        if (entity == null) {
            throw Problems.NULL_OBJECT_PROVIDED_ERROR.toException();
        }

        try {
            return asDomainObject(entity);
        } catch (Exception ex) {
            var problem = new Problem("Mapper failure",
                    "An issue occurred while converting from Entity to DomainObject", 500,
                    "Internal Server Error", "", "00001011", List.of());
            throw  problem.toException();
        }
    }

    default E toEntity(T domainObj) {
        if (domainObj == null) {
            throw Problems.NULL_OBJECT_PROVIDED_ERROR.toException();
        }

        try {
            return asEntity(domainObj);
        } catch (Exception ex) {
            var problem = new Problem("Mapper failure",
                    "An issue occurred while converting from DomainObject to Entity", 500,
                    "Internal Server Error", "", "00001011", List.of());
            throw  problem.toException();
        }

    }

    default List<T> toDomainObjects(List<E> entities) {
        if (entities == null) {
            throw Problems.NULL_OBJECT_PROVIDED_ERROR.toException();
        }

        try {
            return asDomainObjects(entities);
        } catch (Exception ex) {
            // Failures happened should log and return a Future failure
            var problem = new Problem("Mapper failure",
                    "An issue occurred while converting from Entities to DomainObjects", 500,
                    "Internal Server Error", "", "00001012", List.of());
            throw problem.toException();
        }
    }

    default List<E> toEntities(List<T> domainObjs) {
        if (domainObjs == null) {
            throw Problems.NULL_OBJECT_PROVIDED_ERROR.toException();
        }
        try {
            return asEntities(domainObjs);
        } catch (Exception ex) {
            var problem = new Problem("Mapper failure",
                    "An issue occurred while converting from DomainObjects to Entities", 500,
                    "Internal Server Error", "", "00001012", List.of());
            throw problem.toException();
        }
    }


}