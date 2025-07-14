package com.chanbinme.envers.revision.entity;

import com.chanbinme.envers.revision.listener.CustomRevisionListener;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

/**
 * Envers에서 사용하는 커스텀 리비전 엔티티.
 * 기본 리비전 엔티티에 IP 주소 필드를 추가한다.
 */
@Getter
@Setter
@Entity
@Table(name = "revinfo")    // Envers에서 사용하는 기본 테이블 이름
@RevisionEntity(CustomRevisionListener.class) // 리스너 연결
public class CustomRevisionEntity extends DefaultRevisionEntity {

    private String ipAddress;
}
