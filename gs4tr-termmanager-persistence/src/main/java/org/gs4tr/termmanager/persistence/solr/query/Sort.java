package org.gs4tr.termmanager.persistence.solr.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Sort option for queries. You have to provide at least a list of properties to
 * sort for that must not include {@literal null} or empty strings. The default
 * direction is ascending.
 */
public class Sort {

    public static final Direction DEFAULT_DIRECTION = Direction.ASC;
    private List<Order> _orders;

    /**
     * Creates a new {@link Sort} instance.
     *
     * @param direction
     *            Default order is ascending
     *
     * @param properties
     *            must not be {@literal null} or contain {@literal null} or empty
     *            strings.
     *
     */
    public Sort(Direction direction, List<String> properties) {
	if (properties == null || properties.isEmpty()) {
	    throw new IllegalArgumentException("You have to provide at least one property to sort by!");
	}

	_orders = new ArrayList<Order>(properties.size());

	for (String property : properties) {
	    _orders.add(new Order(property, direction));
	}
    }

    /**
     * Creates a new {@link Sort} instance.
     *
     *
     * @param properties
     *            must not be {@literal null}, empty or contain {@literal null} or
     *            empty strings.
     */
    public Sort(Direction direction, String... properties) {
	this(direction, properties == null ? new ArrayList<String>() : Arrays.asList(properties));
    }

    /**
     * Creates a new {@link Sort} instance.
     *
     * @param orders
     *            must not be {@literal null} or contain {@literal null}.
     */
    public Sort(List<Order> orders) {
	if (orders == null || orders.isEmpty()) {
	    throw new IllegalArgumentException("You have to provide at least one sort property to sort by!");
	}

	_orders = orders;
    }

    /**
     * Creates a new {@link Sort} instance using the given {@link Order}s.
     *
     * @param orders
     *            must not be {@literal null}.
     */
    public Sort(Order... orders) {
	this(Arrays.asList(orders));
    }

    /**
     * Creates a new {@link Sort} instance. Default order is ascending
     *
     * @param properties
     *            must not be {@literal null} or contain {@literal null} or empty
     *            strings
     */
    public Sort(String... properties) {
	this(DEFAULT_DIRECTION, properties);
    }

    public List<Order> getOrders() {
	return _orders;
    }

    /**
     * Enumeration for sort directions.
     */
    public enum Direction
    {
	ASC, DESC
    }

    /**
     * Order implements the pairing of an {@link Direction} and a property. It is
     * used to provide input for {@link Sort}
     */
    public static class Order {

	private Direction _direction;

	private String _property;

	/**
	 * Creates a new {@link Order} instance. Takes a single property. Default
	 * direction is ascending.
	 *
	 * @param property
	 *            must not be {@literal null} or empty.
	 */
	public Order(String property) {
	    if (StringUtils.isEmpty(property)) {
		throw new IllegalArgumentException("Property must not null or empty!");
	    }
	    _property = property;
	    _direction = DEFAULT_DIRECTION;
	}

	/**
	 * Creates a new {@link Order} instance. Takes a single property. Default
	 * direction is ascending.
	 *
	 * @param property
	 *            must not be {@literal null} or empty.
	 *
	 * @param direction
	 *            can be {@literal null}, will default to ascending
	 */
	public Order(String property, Direction direction) {
	    this(property);
	    _direction = direction != null ? direction : DEFAULT_DIRECTION;
	}

	public Direction getDirection() {
	    return _direction;
	}

	public String getProperty() {
	    return _property;
	}

	public boolean isAscending() {
	    return Direction.ASC == getDirection();
	}

	public boolean isDescending() {
	    return Direction.DESC == getDirection();
	}

	public void setDirection(Direction direction) {
	    _direction = direction;
	}

	public void setProperty(String property) {
	    _property = property;
	}
    }
}
