/*
  An immutable simple queue trait. Follows the first come first out principle.
 */
trait SimpleQueue[T] {

  // Check if the queue is empty
  def isEmpty: Boolean

  // Query the head element from the queue
  def head: Option[T]

  // Query the Nth element from the queue
  def get(n: Int): T

  // Appends one or more elements at the end of the immutable queue, and returns the new queue.
  def enQueue(t: T*): SimpleQueue[T]

  // Removes the first n element at the beginning of the immutable queue, and returns the new queue.
  def deQueue(n: Int): SimpleQueue[T]
}

/*
  A trait to calculate the moving average.
 */
trait MovingAverage {
  // calculate the moving average of the last N elements
  def movingAverageOfLast(n: Int): BigDecimal
}

/*
  An immutable moving average queue implementation with the ability to calculate the moving average of the last N added elements.
 */
class MovingAverageQueue private(array: Array[BigDecimal]) extends SimpleQueue[BigDecimal] with MovingAverage {

  // Check if the queue is empty
  override def isEmpty: Boolean = array.isEmpty

  // Query the head element from the queue
  override def head: Option[BigDecimal] = array.headOption

  // Query the Nth element from the queue
  override def get(n: Int): BigDecimal = {
    if (n > array.size) throw new IllegalArgumentException(s"$n is over the length")
    else if (n < 0) throw new IllegalArgumentException("can not accept negative number")
    else array(n)
  }

  // Appends one or more elements at the end of the immutable queue, and returns the new queue.
  override def enQueue(t: BigDecimal*): MovingAverageQueue = {
    new MovingAverageQueue(array ++ t)
  }

  // Removes the first n element at the beginning of the immutable queue, and returns the new queue.
  override def deQueue(n: Int = 1): MovingAverageQueue = {
    if (n <= 0) throw new IllegalArgumentException("can only accept positive number")
    new MovingAverageQueue(array.drop(n))
  }

  // calculate the moving average of the last N elements
  override def movingAverageOfLast(n: Int): BigDecimal = {
    if (n <= 0) throw new IllegalArgumentException("can only accept positive number")
    else array.takeRight(n).sum / n
  }
}

/*
  The factory of moving average queue implementation.
 */
object MovingAverageQueue {
  def apply(array: BigDecimal*): MovingAverageQueue = new MovingAverageQueue(array.toArray)
}

// works like a test case
object Run extends App {
  var i: BigDecimal = 0;
  var q = MovingAverageQueue(11, 22.2, 0.33)
  q = q.enQueue(4)
  q = q.enQueue(5.5, 666)
  i = q.movingAverageOfLast(6)
  assert(i == BigDecimal("118.1716666666666666666666666666667"))

  q = q.deQueue()
  q = q.enQueue(2)
  i = q.movingAverageOfLast(3)
  assert(i == BigDecimal("224.5"))

  q = q.deQueue(100)
  i = q.movingAverageOfLast(30)
  assert(i == BigDecimal("0"))
}