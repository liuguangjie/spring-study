package com.spring.study.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;

/**
 * Created by free on 17-1-31.
 */
@Aspect
public class AspectJAop {
    @Pointcut("execution(* printHelloWorld(..))")
    private void exp(){

    }

    @Before("exp()")
    public void aspectBefore(JoinPoint joinPoint){
        System.out.println("方法名字 "+ joinPoint.getSignature().getName());
        //System.out.println("aspectBefore");
    }


    @Around("exp()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        System.out.println("环绕通知..."+pjp.getArgs());
        Object obj=pjp.proceed();
        System.out.println("结束环绕通知...");
        return obj;
    }

    @AfterThrowing(value = "exp()",throwing = "ex")
    public void doThrow(JoinPoint joinPoint,Throwable ex){
        System.out.println(ex.getMessage()+"出现异常了");
    }

    @AfterReturning(value = "exp()",returning = "test")
    public void afterRet(JoinPoint joinPoint,Object test){
        System.out.println("方法的返回值"+test);
    }
}
