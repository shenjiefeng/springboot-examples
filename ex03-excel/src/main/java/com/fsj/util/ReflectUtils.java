package com.fsj.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
* Java���乤����
* @author <a href="xdemo.org">xdemo.org</a>
*
*/
public class ReflectUtils {
	/**
	 * ��ȡ��Ա���������η�
	 * @param clazz
	 * @param field
	 * @return
	 * @throws Exception
	 */
	public static <T> int getFieldModifier(Class<T> clazz, String field) throws Exception {
		//getDeclaredFields���Ի�ȡ�������η��ĳ�Ա����������private,protected��getFields�򲻿���
		Field[] fields = clazz.getDeclaredFields();

		for (int i = 0; i < fields.length; i++) {
			if (fields[i].getName().equals(field)) {
				return fields[i].getModifiers();
			}
		}
		throw new Exception(clazz+" has no field \"" + field + "\"");
	}

	/**
	 * ��ȡ��Ա���������η�
	 * @param clazz
	 * @param method
	 * @return
	 * @throws Exception
	 */
	public static <T> int getMethodModifier(Class<T> clazz, String method) throws Exception {
		
		//getDeclaredMethods���Ի�ȡ�������η��ĳ�Ա����������private,protected��getMethods�򲻿���
		Method[] m = clazz.getDeclaredMethods();

		for (int i = 0; i < m.length; i++) {
			if (m[i].getName().equals(m)) {
				return m[i].getModifiers();
			}
		}
		throw new Exception(clazz+" has no method \"" + m + "\"");
	}

	/**
	 *  [����]���ݳ�Ա�������ƻ�ȡ��ֵ
	 * @param clazzInstance
	 * @param field
	 * @return
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static <T> Object getFieldValue(Object clazzInstance, Object field) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

		Field[] fields = clazzInstance.getClass().getDeclaredFields();

		for (int i = 0; i < fields.length; i++) {
			if (fields[i].getName().equals(field)) {
				//����˽�б����ķ���Ȩ�ޣ����������ã��������ɷ���Private���εı���
				fields[i].setAccessible(true);
				return fields[i].get(clazzInstance);
			}
		}

		return null;
	}

	/**
	 *[��]���ݳ�Ա�������ƻ�ȡ��ֵ��Ĭ��ֵ��
	 * @param clazz
	 * @param field
	 * @return
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static <T> Object getFieldValue(Class<T> clazz, String field) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InstantiationException {

		Field[] fields = clazz.getDeclaredFields();

		for (int i = 0; i < fields.length; i++) {
			if (fields[i].getName().equals(field)) {
				//����˽�б����ķ���Ȩ�ޣ����������ã��������ɷ���Private���εı���
				fields[i].setAccessible(true);
				return fields[i].get(clazz.newInstance());
			}
		}

		return null;
	}

	/**
	 * ��ȡ���еĳ�Ա����
	 * @param clazz
	 * @return
	 */
	public static <T> String[] getFields(Class<T> clazz) {

		Field[] fields = clazz.getDeclaredFields();

		String[] fieldsArray = new String[fields.length];

		for (int i = 0; i < fields.length; i++) {
			fieldsArray[i] = fields[i].getName();
		}

		return fieldsArray;
	}

	/**
	 * ��ȡ���еĳ�Ա����,��������
	 * @param clazz
	 * @return
	 * @throws Exception 
	 */
	public static <T> Field[] getClassFieldsAndSuperClassFields(Class<T> clazz) throws Exception {

		Field[] fields = clazz.getDeclaredFields();
		
		if(clazz.getSuperclass()==null){
			throw new Exception(clazz.getName()+"û�и���");
		}

		Field[] superFields = clazz.getSuperclass().getDeclaredFields();

		
		Field[] allFields=new Field[fields.length+superFields.length];
		
		for(int i=0;i<fields.length;i++){
			allFields[i]=fields[i];
		}
		for(int i=0;i<superFields.length;i++){
			allFields[fields.length+i]=superFields[i];
		}

		return allFields;
	}

	/**
	 * 拿到父类及子类的field，如果子类重写了父类的field则以子类为准
	 * @param clazz
	 * @param <T>
	 * @return
	 * @throws Exception
	 */
	public static <T> Field[] getFieldsRemoveSame(Class<T> clazz) throws Exception {

		Field[] fields = clazz.getDeclaredFields();

		if(clazz.getSuperclass()==null){
			throw new Exception(clazz.getName()+"û�и���");
		}

		Field[] superFields = clazz.getSuperclass().getDeclaredFields();


		Field[] allFields=new Field[fields.length+superFields.length];

		System.arraycopy(superFields,0,allFields,0,superFields.length);

		System.arraycopy(fields,0,allFields,superFields.length,fields.length);

		Map<String,Field> map = new HashMap<>();
		//去掉父类的名称相同的
		for(Field field : allFields){
			map.put(field.getName(),field);
		}

		return map.values().toArray(new Field[map.values().size()]);
	}
	/**
	 * ָ���࣬����ָ�����޲η���
	 * @param clazz
	 * @param method
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 */
	public static <T> Object invoke(Class<T> clazz, String method) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		Object instance = clazz.newInstance();
		Method m = clazz.getMethod(method, new Class[] {});
		return m.invoke(instance, new Object[] {});
	}

	/**
	 * ͨ�����󣬷����䷽��
	 * 
	 * @param clazzInstance
	 * @param method
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 */
	public static <T> Object invoke(Object clazzInstance, String method) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		Method m = clazzInstance.getClass().getMethod(method, new Class[] {});
		return m.invoke(clazzInstance, new Object[] {});
	}

	/**
	 * ָ���࣬����ָ���ķ���
	 * 
	 * @param clazz
	 * @param method
	 * @param paramClasses
	 * @param params
	 * @return Object
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public static <T> Object invoke(Class<T> clazz, String method, Class<T>[] paramClasses, Object[] params) throws InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
		Object instance = clazz.newInstance();
		Method _m = clazz.getMethod(method, paramClasses);
		return _m.invoke(instance, params);
	}

	/**
	 * ͨ�����ʵ��������ָ���ķ���
	 * 
	 * @param clazzInstance
	 * @param method
	 * @param paramClasses
	 * @param params
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public static <T> Object invoke(Object clazzInstance, String method, Class<T>[] paramClasses, Object[] params) throws InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
		Method _m = clazzInstance.getClass().getMethod(method, paramClasses);
		return _m.invoke(clazzInstance, params);
	}

//	@SuppressWarnings("unchecked")
//	public static void main(String[] args) throws Exception {
//		// getFields(User.class);
//		User u = new User();
//		invoke(u, "setName", new Class[] { String.class }, new Object[] { "xx����ˮ����ˮ��x" });
//		System.out.println(getFieldValue(u, "name"));
//	}
}
