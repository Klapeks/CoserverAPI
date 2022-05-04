package com.klapeks.funcs;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_PARAMETER;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;

@Documented
@Target({ FIELD, METHOD, PARAMETER, TYPE_PARAMETER })
public @interface NotNull {}
