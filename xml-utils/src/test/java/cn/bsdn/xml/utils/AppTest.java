package cn.bsdn.xml.utils;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {

	@BeforeClass
	public static void setUp() {
		System.out.println("beforeClass");
	}
	@AfterClass
	public static void tearDown() {
		System.out.println("AfterClass");
	}
	@Before
	public void before() {
		System.out.println("before");
	}
	@After
	public void after() {
		System.out.println("after");
	}
	@Test
	public void method1() {
		System.out.println("method1");
	}

	@Test
	public void method2() {
		System.out.println("method2");
	}
}
