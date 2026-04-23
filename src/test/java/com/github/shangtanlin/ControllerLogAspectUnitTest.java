package com.github.shangtanlin;

import com.github.shangtanlin.aspect.ControllerLogAspect;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 纯单元测试，验证切面逻辑是否正确
 */
@ExtendWith(MockitoExtension.class)
class ControllerLogAspectUnitTest {

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    private ControllerLogAspect aspect;

    @BeforeEach
    void setUp() {
        aspect = new ControllerLogAspect();
    }

    @Test
    void testAspectClassExists() {
        System.out.println("===== 切面类验证 =====");
        System.out.println("切面类: " + ControllerLogAspect.class.getName());
        System.out.println("是否存在 @Aspect 注解: " +
                ControllerLogAspect.class.isAnnotationPresent(org.aspectj.lang.annotation.Aspect.class));
        System.out.println("是否存在 @Component 注解: " +
                ControllerLogAspect.class.isAnnotationPresent(org.springframework.stereotype.Component.class));

        assertTrue(ControllerLogAspect.class.isAnnotationPresent(org.aspectj.lang.annotation.Aspect.class),
                "类应该有 @Aspect 注解");
        assertTrue(ControllerLogAspect.class.isAnnotationPresent(org.springframework.stereotype.Component.class),
                "类应该有 @Component 注解");
    }

    @Test
    void testAroundMethodExists() throws Exception {
        // 验证 around 方法存在
        Method aroundMethod = ControllerLogAspect.class.getDeclaredMethod("around", ProceedingJoinPoint.class);
        System.out.println("===== around 方法验证 =====");
        System.out.println("around 方法存在: " + (aroundMethod != null));
        System.out.println("是否有 @Around 注解: " +
                aroundMethod.isAnnotationPresent(org.aspectj.lang.annotation.Around.class));

        assertTrue(aroundMethod.isAnnotationPresent(org.aspectj.lang.annotation.Around.class),
                "around 方法应该有 @Around 注解");
    }

    @Test
    void testAroundLogic() throws Throwable {
        // 模拟 MethodSignature
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getDeclaringType()).thenReturn((Class) TestController.class);
        when(methodSignature.getName()).thenReturn("testMethod");
        when(methodSignature.getParameterNames()).thenReturn(new String[]{"param1", "param2"});
        when(joinPoint.getArgs()).thenReturn(new Object[]{"value1", "value2"});
        when(joinPoint.proceed()).thenReturn("mockResult");

        System.out.println("===== 切面逻辑测试 =====");

        // 执行切面方法
        Object result = aspect.around(joinPoint);

        System.out.println("返回值: " + result);
        System.out.println("joinPoint.proceed() 是否被调用: " +
                (mockingDetails(joinPoint).getInvocations().stream()
                        .anyMatch(i -> i.getMethod().getName().equals("proceed"))));

        // 验证
        assertEquals("mockResult", result);
        verify(joinPoint).proceed();  // 验证目标方法被调用
    }

    @Test
    void testPointcutMethodExists() throws Exception {
        Method pointcutMethod = ControllerLogAspect.class.getDeclaredMethod("controllerPointcut");
        System.out.println("===== pointcut 方法验证 =====");
        System.out.println("pointcut 方法存在: " + (pointcutMethod != null));
        System.out.println("是否有 @Pointcut 注解: " +
                pointcutMethod.isAnnotationPresent(org.aspectj.lang.annotation.Pointcut.class));

        assertTrue(pointcutMethod.isAnnotationPresent(org.aspectj.lang.annotation.Pointcut.class),
                "controllerPointcut 方法应该有 @Pointcut 注解");
    }

    @Test
    void testPointcutExpression() throws Exception {
        Method pointcutMethod = ControllerLogAspect.class.getDeclaredMethod("controllerPointcut");
        org.aspectj.lang.annotation.Pointcut pointcut =
                pointcutMethod.getAnnotation(org.aspectj.lang.annotation.Pointcut.class);

        System.out.println("===== 切点表达式验证 =====");
        System.out.println("切点表达式: " + pointcut.value());

        // 验证切点表达式匹配 controller 包
        assertTrue(pointcut.value().contains("controller"),
                "切点表达式应该匹配 controller 包");
    }

    /**
     * 测试用的 Controller 类
     */
    static class TestController {
        public String testMethod(String param1, String param2) {
            return param1 + param2;
        }
    }
}