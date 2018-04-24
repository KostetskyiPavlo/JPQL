package com.logos.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "city")
@NoArgsConstructor
@Getter @Setter
@ToString(callSuper = true, exclude = {"country", "users"})
public class City extends BaseEntity {
	
	@Column(name = "name")
	private String name;
	
	@ManyToOne(cascade = {
			CascadeType.DETACH, CascadeType.MERGE, 
			CascadeType.PERSIST, CascadeType.REFRESH
			}, fetch = FetchType.LAZY)
	@JoinColumn(name = "country_id")
	private Country country;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "city")
	private List<User> users = new ArrayList<>();

}
