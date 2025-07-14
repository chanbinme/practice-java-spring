package com.chanbinme.envers.revision.listener;

import com.chanbinme.envers.revision.entity.CustomRevisionEntity;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.envers.RevisionListener;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class CustomRevisionListener implements RevisionListener {

    /**
     * Envers에서 새로운 리비전이 생성될 때 호출되는 메소드.
     * 현재 요청의 IP 주소를 CustomRevisionEntity에 설정한다.
     *
     * @param revisionEntity 새로 생성된 리비전 엔티티
     */
    @Override
    public void newRevision(Object revisionEntity) {
        CustomRevisionEntity rev = (CustomRevisionEntity) revisionEntity;

        String ip = "unknown";

        // 현재 요청의 HttpServletRequest에서 IP 추출
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty()) {
                ip = request.getRemoteAddr();
            }
        }
        rev.setIpAddress(ip);
    }
}
