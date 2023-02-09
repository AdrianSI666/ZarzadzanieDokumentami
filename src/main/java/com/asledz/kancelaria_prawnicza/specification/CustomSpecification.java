package com.asledz.kancelaria_prawnicza.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
public class CustomSpecification<T> implements Specification<T>, Serializable {
    private SearchCriteria criteria;

    @Override
    public Predicate toPredicate(@NonNull Root<T> root,
                                 @NonNull CriteriaQuery<?> query,
                                 @NonNull CriteriaBuilder builder) {
        if (criteria.getOperation().equalsIgnoreCase(">")) {
            return builder.greaterThanOrEqualTo(
                    root.get(criteria.getKey()), criteria.getValue().toString());
        } else if (criteria.getOperation().equalsIgnoreCase("<")) {
            return builder.lessThanOrEqualTo(
                    root.get(criteria.getKey()), criteria.getValue().toString());
        } else if (criteria.getOperation().equalsIgnoreCase(":")) {
            if (criteria.getKey().contains("_")) {
                String[] value = criteria.getKey().split("_");
                Join<Object, Object> listJoin = root.join(value[0], JoinType.INNER);
                return builder.equal(listJoin.get(value[1]), criteria.getValue());
            }
            if (root.get(criteria.getKey()).getJavaType() == String.class) {
                query.orderBy(builder.desc(root.get("id")));
                return builder.like(
                        root.get(criteria.getKey()), "%" + criteria.getValue() + "%");
            } else {
                return builder.equal(root.get(criteria.getKey()), criteria.getValue());
            }
        }
        return null;
    }
}
