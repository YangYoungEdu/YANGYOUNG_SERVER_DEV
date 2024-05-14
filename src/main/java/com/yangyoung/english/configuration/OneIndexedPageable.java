package com.yangyoung.english.configuration;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.OffsetScrollPosition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;

public class OneIndexedPageable implements Pageable {

    private final Pageable delegate;

    public OneIndexedPageable(Pageable delegate) {
        // 페이지 번호를 1 감소시켜서 0부터 시작하도록 조정
        this.delegate = delegate.getPageNumber() == 0 ? delegate : delegate.previousOrFirst();
    }

    @Override
    public boolean isPaged() {
        return Pageable.super.isPaged();
    }

    @Override
    public boolean isUnpaged() {
        return Pageable.super.isUnpaged();
    }

    @Override
    public int getPageNumber() {
        // 실제 페이지 번호를 1 증가시켜서 반환
        return delegate.getPageNumber() + 1;
    }

    @Override
    public int getPageSize() {
        return delegate.getPageSize();
    }

    @Override
    public long getOffset() {
        return delegate.getOffset();
    }

    @Override
    public Sort getSort() {
        return delegate.getSort();
    }

    @Override
    public Sort getSortOr(Sort sort) {
        return Pageable.super.getSortOr(sort);
    }

    @Override
    public Pageable next() {
        return new OneIndexedPageable(delegate.next());
    }

    @Override
    public Pageable previousOrFirst() {
        return new OneIndexedPageable(delegate.previousOrFirst());
    }

    @Override
    public Pageable first() {
        return new OneIndexedPageable(delegate.first());
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return null;
    }

    @Override
    public boolean hasPrevious() {
        return delegate.hasPrevious();
    }

    @Override
    public Optional<Pageable> toOptional() {
        return Pageable.super.toOptional();
    }

    @Override
    public Limit toLimit() {
        return Pageable.super.toLimit();
    }

    @Override
    public OffsetScrollPosition toScrollPosition() {
        return Pageable.super.toScrollPosition();
    }
}

