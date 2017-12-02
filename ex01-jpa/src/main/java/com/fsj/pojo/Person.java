package com.fsj.pojo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;

@Entity //1 实体类，和数据库表映射
//支持用JPA的NameQuery来定义查询方法，一个名称映射一个查询语句
@NamedQuery(name = "Person.withNameAndAddressNamedQuery",
			query = "select p from Person p where p.name=?1 and address=?2")
public class Person {
	@Id //2 主键

	/* 3 默认主键生成方式为自增。hibernate自动生成名为HIBERNATE_SEQUENCE的序列。
	* 插入数据时可使用此序列，eg：
	* insert into person(id,name,age,address) values(hibernate_sequence.nextval,'xx',31,'北京');
	* */
	@GeneratedValue
	private Long id;
	
	private String name;
	
	private Integer age;
	
	private String address;
	
	
	
	public Person() {
		super();
	}
	public Person(Long id, String name, Integer age, String address) {
		this.id = id;
		this.name = name;
		this.age = age;
		this.address = address;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}


}
