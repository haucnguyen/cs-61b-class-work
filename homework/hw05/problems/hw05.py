def print_move(origin, destination):
    """Print instructions to move a disk."""
    print("Move the top disk from rod", origin, "to rod", destination)

def move_stack(n, start, end):
    """Print the moves required to move n disks on the start pole to the end
    pole without violating the rules of Towers of Hanoi.

    n -- number of disks
    start -- a pole position, either 1, 2, or 3
    end -- a pole position, either 1, 2, or 3

    There are exactly three poles, and start and end must be different. Assume
    that the start pole has at least n disks of increasing size, and the end
    pole is either empty or has a top disk larger than the top n start disks.

    >>> move_stack(1, 1, 3)
    Move the top disk from rod 1 to rod 3
    >>> move_stack(2, 1, 3)
    Move the top disk from rod 1 to rod 2
    Move the top disk from rod 1 to rod 3
    Move the top disk from rod 2 to rod 3
    >>> move_stack(3, 1, 3)
    Move the top disk from rod 1 to rod 3
    Move the top disk from rod 1 to rod 2
    Move the top disk from rod 3 to rod 2
    Move the top disk from rod 1 to rod 3
    Move the top disk from rod 2 to rod 1
    Move the top disk from rod 2 to rod 3
    Move the top disk from rod 1 to rod 3
    """
    assert 1 <= start <= 3 and 1 <= end <= 3 and start != end, "Bad start/end"
    spare = 6 - start - end
    if n == 1:
        return print_move(start, end)
    else:
        move_stack(n - 1, start, spare)
        move_stack(1, start, end)
        move_stack(n-1, spare, end)


def interval(a, b):
    """Construct an interval from a to b."""
    return [a, b]

def lower_bound(x):
    """Return the lower bound of interval x."""
    return min(x)

def upper_bound(x):
    """Return the upper bound of interval x."""
    return max(x)

def str_interval(x):
    """Return a string representation of interval x."""
    return '{0} to {1}'.format(lower_bound(x), upper_bound(x))

def add_interval(x, y):
    """Return an interval that contains the sum of any value in interval x and
    any value in interval y."""
    lower = lower_bound(x) + lower_bound(y)
    upper = upper_bound(x) + upper_bound(y)
    return interval(lower, upper)

def mul_interval(x, y):
    """Return the interval that contains the product of any value in x and any
    value in y."""
    """p1 = x[0] * y[0]
    p2 = x[0] * y[1]
    p3 = x[1] * y[0]
    p4 = x[1] * y[1]
    return [min(p1, p2, p3, p4), max(p1, p2, p3, p4)]
    """
    lower = lower_bound(x) * upper_bound(y)
    upper = upper_bound(x) * upper_bound(y)
    return interval(lower, upper)

def sub_interval(x, y):
    """Return the interval that contains the difference between any value in x
    and any value in y."""
    lower = lower_bound(x) - upper_bound(y)
    uppper = upper_bound(x) - lower_bound(y)
    return interval(lower, uppper)

def div_interval(x, y):
    """Return the interval that contains the quotient of any value in x divided by
    any value in y. Division is implemented as the multiplication of x by the
    reciprocal of y."""
    assert lower_bound(y) > 0 and upper_bound(y) > 0 or lower_bound(y) < 0 and upper_bound(y) < 0, "Can't divide by zero!"
    reciprocal_y = interval(1/upper_bound(y), 1/lower_bound(y))
    return mul_interval(x, reciprocal_y)

def par1(r1, r2):
    return div_interval(mul_interval(r1, r2), add_interval(r1, r2))

def par2(r1, r2):
    one = interval(1, 1)
    rep_r1 = div_interval(one, r1)
    rep_r2 = div_interval(one, r2)
    return div_interval(one, add_interval(rep_r1, rep_r2))

def check_par():
    """Return two intervals that give different results for parallel resistors.

    >>> r1, r2 = check_par()
    >>> x = par1(r1, r2)
    >>> y = par2(r1, r2)
    >>> lower_bound(x) != lower_bound(y) or upper_bound(x) != upper_bound(y)
    True
    """
    r1 = interval(3, 4) # Replace this line!
    r2 = interval(5, 6) # Replace this line!
    return r1, r2

def multiple_references_explanation():
    return """The multiple reference problem occurs when par1() calls mul_interval(r1, r2), \n
    which mutates r1 and r2. par1() then calls add_interval(r1, r2), with r1 and r2 now mutated. \n
    This results in add_interval() returning a different interval to be called in div_interval(). \n
    This is why Eva Lu Ator is correct when she says that par2 is a better program for resistances, \n
    because it assigns the result of div_interval() on both intervals to separate variables and then \n
    processes them. This separation/copying will result in the correct interval."""


def quadratic(x, a, b, c):
    """Return the interval that is the range of the quadratic defined by
    coefficients a, b, and c, for domain interval x.

    >>> str_interval(quadratic(interval(0, 2), -2, 3, -1))
    '-3 to 0.125'
    >>> str_interval(quadratic(interval(1, 3), 2, -3, 1))
    '0 to 10'
    """
    def f(t):
        return a*t*t + b*t + c

    quadratic_extreme = -b/(2*a)

    extreme_bound_of_f, lower_bound_of_f, upper_bound_of_f = f(quadratic_extreme), f(lower_bound(x)), f(upper_bound(x))
    a = [lower_bound_of_f, extreme_bound_of_f, upper_bound_of_f]
    b = [lower_bound_of_f, upper_bound_of_f]
    if lower_bound(x) <= quadratic_extreme <= upper_bound(x):
        return interval(min(a), max(a))
    else:
        return interval(min(b), max(b))

    # for x in x
    # lower_interval = min(x)
    # upper_interval = max(x)
    # lower = (a * lower_interval**2) + (b * lower_interval) + c
    # # upper = (2 * a * upper_interval) + b
    # upper = (a * upper_interval**2) + (b * upper_interval) + c
    # return interval(lower, upper)

def polynomial(x, c):
    """Return the interval that is the range of the polynomial defined by
    coefficients c, for domain interval x.

    >>> str_interval(polynomial(interval(0, 2), [-1, 3, -2]))
    '-3 to 0.125'
    >>> str_interval(polynomial(interval(1, 3), [1, -3, 2]))
    '0 to 10'
    >>> str_interval(polynomial(interval(0.5, 2.25), [10, 24, -6, -8, 3]))
    '18.0 to 23.0'
    """
    "*** YOUR CODE HERE ***"
