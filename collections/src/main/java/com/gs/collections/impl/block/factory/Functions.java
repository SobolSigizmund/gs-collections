/*
 * Copyright 2015 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gs.collections.impl.block.factory;

import java.util.Map;

import com.gs.collections.api.block.SerializableComparator;
import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.block.function.Function2;
import com.gs.collections.api.block.function.primitive.BooleanFunction;
import com.gs.collections.api.block.function.primitive.ByteFunction;
import com.gs.collections.api.block.function.primitive.CharFunction;
import com.gs.collections.api.block.function.primitive.DoubleFunction;
import com.gs.collections.api.block.function.primitive.FloatFunction;
import com.gs.collections.api.block.function.primitive.IntFunction;
import com.gs.collections.api.block.function.primitive.LongFunction;
import com.gs.collections.api.block.function.primitive.ShortFunction;
import com.gs.collections.api.block.predicate.Predicate;
import com.gs.collections.api.block.procedure.Procedure;
import com.gs.collections.api.block.procedure.Procedure2;
import com.gs.collections.api.block.procedure.primitive.ObjectIntProcedure;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.block.comparator.primitive.BooleanFunctionComparator;
import com.gs.collections.impl.block.comparator.primitive.ByteFunctionComparator;
import com.gs.collections.impl.block.comparator.primitive.CharFunctionComparator;
import com.gs.collections.impl.block.comparator.primitive.DoubleFunctionComparator;
import com.gs.collections.impl.block.comparator.primitive.FloatFunctionComparator;
import com.gs.collections.impl.block.comparator.primitive.IntFunctionComparator;
import com.gs.collections.impl.block.comparator.primitive.LongFunctionComparator;
import com.gs.collections.impl.block.comparator.primitive.ShortFunctionComparator;
import com.gs.collections.impl.block.function.CaseFunction;
import com.gs.collections.impl.block.function.IfFunction;
import com.gs.collections.impl.block.function.checked.CheckedFunction;
import com.gs.collections.impl.block.function.checked.ThrowingFunction;
import com.gs.collections.impl.block.function.primitive.IntegerFunctionImpl;
import com.gs.collections.impl.tuple.Tuples;
import com.gs.collections.impl.utility.Iterate;
import com.gs.collections.impl.utility.StringIterate;

public final class Functions
{
    private static final Function<Double, Double> DOUBLE_PASS_THRU_FUNCTION = new DoublePassThruFunction();
    private static final Function<Integer, Integer> INTEGER_PASS_THRU_FUNCTION = new IntegerPassThruFunction();
    private static final Function<Long, Long> LONG_PASS_THRU_FUNCTION = new LongPassThruFunction();

    private static final Function<Object, Boolean> TRUE_FUNCTION = new TrueFunction();
    private static final Function<Object, Boolean> FALSE_FUNCTION = new FalseFunction();
    private static final Function<?, ?> PASS_THRU_FUNCTION = new PassThruFunction<Object>();
    private static final Function<String, String> STRING_TRIM_FUNCTION = new StringTrimFunction();
    private static final Function<Object, Class<?>> CLASS_FUNCTION = new ClassFunction();
    private static final Function<Number, Double> MATH_SIN_FUNCTION = new MathSinFunction();
    private static final Function<Integer, Integer> SQUARED_INTEGER = new SquaredIntegerFunction();
    private static final Function<Object, String> TO_STRING_FUNCTION = new ToStringFunction();
    private static final Function<String, Integer> STRING_TO_INTEGER_FUNCTION = new StringToIntegerFunction();
    private static final Function<?, ?> MAP_KEY_FUNCTION = new MapKeyFunction<Object>();
    private static final Function<?, ?> MAP_VALUE_FUNCTION = new MapValueFunction<Object>();
    private static final Function<Iterable<?>, Integer> SIZE_FUNCTION = new SizeFunction();
    private static final FirstOfPairFunction<?> FIRST_OF_PAIR_FUNCTION = new FirstOfPairFunction<Object>();
    private static final SecondOfPairFunction<?> SECOND_OF_PAIR_FUNCTION = new SecondOfPairFunction<Object>();
    private static final CheckedFunction<String, Class<?>> CLASS_FOR_NAME = new ClassForNameFunction();
    private static final SwappedPairFunction<?, ?> SWAPPED_PAIR_FUNCTION = new SwappedPairFunction<Object, Object>();

    private Functions()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    private static class PassThruFunction<T> implements Function<T, T>
    {
        private static final long serialVersionUID = 1L;

        public T valueOf(T anObject)
        {
            return anObject;
        }
    }

    private static class StringTrimFunction implements Function<String, String>
    {
        private static final long serialVersionUID = 1L;

        public String valueOf(String s)
        {
            return s.trim();
        }
    }

    private static final class FixedValueFunction<T, V> implements Function<T, V>
    {
        private static final long serialVersionUID = 1L;
        private final V value;

        private FixedValueFunction(V value)
        {
            this.value = value;
        }

        public V valueOf(T object)
        {
            return this.value;
        }
    }

    private static final class ClassFunction implements Function<Object, Class<?>>
    {
        private static final long serialVersionUID = 1L;

        public Class<?> valueOf(Object anObject)
        {
            return anObject.getClass();
        }

        @Override
        public String toString()
        {
            return "object.getClass()";
        }
    }

    private static final class MathSinFunction implements Function<Number, Double>
    {
        private static final long serialVersionUID = 1L;

        public Double valueOf(Number number)
        {
            return Math.sin(number.doubleValue());
        }

        @Override
        public String toString()
        {
            return "Math.sin()";
        }
    }

    private static final class SquaredIntegerFunction implements Function<Integer, Integer>
    {
        private static final long serialVersionUID = 1L;

        public Integer valueOf(Integer value)
        {
            return value * value;
        }
    }

    private static final class ToStringFunction implements Function<Object, String>
    {
        private static final long serialVersionUID = 1L;

        public String valueOf(Object anObject)
        {
            return String.valueOf(anObject);
        }

        @Override
        public String toString()
        {
            return "toString";
        }
    }

    private static final class StringToIntegerFunction implements Function<String, Integer>
    {
        private static final long serialVersionUID = 1L;

        public Integer valueOf(String string)
        {
            return Integer.valueOf(string);
        }

        @Override
        public String toString()
        {
            return "stringToInteger";
        }
    }

    public static <T, V> Function<T, V> throwing(ThrowingFunction<T, V> throwingFunction)
    {
        return new ThrowingFunctionAdapter<T, V>(throwingFunction);
    }

    /**
     * Allows a Java 8 lambda and method reference to be used in a method taking a Function as a parameter
     * without any ambiguity.
     */
    public static <T, V> Function<T, V> cast(Function<T, V> function)
    {
        return function;
    }

    /**
     * Alias for identity(). Inlineable.
     *
     * @see #identity()
     */
    public static <T> Function<T, T> getPassThru()
    {
        return Functions.identity();
    }

    /**
     * @since 6.0
     */
    public static <T> Function<T, T> identity()
    {
        return (Function<T, T>) PASS_THRU_FUNCTION;
    }

    /**
     * @since 6.0
     */
    public static Function<Object, Boolean> getTrue()
    {
        return TRUE_FUNCTION;
    }

    /**
     * @since 6.0
     */
    public static Function<Object, Boolean> getFalse()
    {
        return FALSE_FUNCTION;
    }

    public static <T, V> Function<T, V> getFixedValue(V value)
    {
        return new FixedValueFunction<T, V>(value);
    }

    public static Function<Object, Class<?>> getToClass()
    {
        return CLASS_FUNCTION;
    }

    public static Function<Number, Double> getMathSinFunction()
    {
        return MATH_SIN_FUNCTION;
    }

    public static Function<Number, Number> getNumberPassThru()
    {
        return (Function<Number, Number>) PASS_THRU_FUNCTION;
    }

    public static Function<Integer, Integer> getIntegerPassThru()
    {
        return INTEGER_PASS_THRU_FUNCTION;
    }

    public static Function<Long, Long> getLongPassThru()
    {
        return LONG_PASS_THRU_FUNCTION;
    }

    public static Function<Double, Double> getDoublePassThru()
    {
        return DOUBLE_PASS_THRU_FUNCTION;
    }

    public static Function<String, String> getStringPassThru()
    {
        return (Function<String, String>) PASS_THRU_FUNCTION;
    }

    public static Function<String, String> getStringTrim()
    {
        return STRING_TRIM_FUNCTION;
    }

    public static Function<Object, String> getToString()
    {
        return TO_STRING_FUNCTION;
    }

    public static Function<Object, String> getNullSafeToString(String defaultValue)
    {
        return Functions.nullSafe(TO_STRING_FUNCTION, defaultValue);
    }

    public static <T> SerializableComparator<T> toBooleanComparator(BooleanFunction<T> function)
    {
        return new BooleanFunctionComparator<T>(function);
    }

    public static <T> SerializableComparator<T> toByteComparator(ByteFunction<T> function)
    {
        return new ByteFunctionComparator<T>(function);
    }

    public static <T> SerializableComparator<T> toCharComparator(CharFunction<T> function)
    {
        return new CharFunctionComparator<T>(function);
    }

    public static <T> SerializableComparator<T> toFloatComparator(FloatFunction<T> function)
    {
        return new FloatFunctionComparator<T>(function);
    }

    public static <T> SerializableComparator<T> toShortComparator(ShortFunction<T> function)
    {
        return new ShortFunctionComparator<T>(function);
    }

    public static <T> SerializableComparator<T> toIntComparator(IntFunction<T> function)
    {
        return new IntFunctionComparator<T>(function);
    }

    public static <T> SerializableComparator<T> toDoubleComparator(DoubleFunction<T> function)
    {
        return new DoubleFunctionComparator<T>(function);
    }

    public static <T> SerializableComparator<T> toLongComparator(LongFunction<T> function)
    {
        return new LongFunctionComparator<T>(function);
    }

    public static Function<String, Integer> getStringToInteger()
    {
        return STRING_TO_INTEGER_FUNCTION;
    }

    public static <T, V> Function<T, V> withDefault(Function<? super T, ? extends V> function, V defaultValue)
    {
        return new DefaultFunction<T, V>(function, defaultValue);
    }

    public static <T, V> Function<T, V> nullSafe(Function<? super T, ? extends V> function)
    {
        return new NullSafeFunction<T, V>(function, null);
    }

    public static <T, V> Function<T, V> nullSafe(Function<? super T, ? extends V> function, V nullValue)
    {
        return new NullSafeFunction<T, V>(function, nullValue);
    }

    public static <V1> Function<Pair<V1, ?>, V1> firstOfPair()
    {
        return (Function<Pair<V1, ?>, V1>) (Function<?, ?>) FIRST_OF_PAIR_FUNCTION;
    }

    public static <V2> Function<Pair<?, V2>, V2> secondOfPair()
    {
        return (Function<Pair<?, V2>, V2>) (Function<?, ?>) SECOND_OF_PAIR_FUNCTION;
    }

    /**
     * Swap the input pair and return the swapped pair.
     *
     * @return A function that gets the swapped pair {@code Iterable}
     */
    public static <S, T> Function<Pair<S, T>, Pair<T, S>> swappedPair()
    {
        return (Function<Pair<S, T>, Pair<T, S>>) (Function<?, ?>) SWAPPED_PAIR_FUNCTION;
    }

    /**
     * Bind the parameter passed to a Function2 into a new Function.
     *
     * @param function  The Function2 to delegate the invocation to.
     * @param parameter The parameter the use in the invocation of the delegate function.
     * @return A new Function
     */
    public static <T, P, R> Function<T, R> bind(Function2<? super T, ? super P, ? extends R> function, P parameter)
    {
        return new BindFunction2<T, P, R>(function, parameter);
    }

    /**
     * Bind the input of a Procedure to the result of an function, returning a new Procedure.
     *
     * @param delegate The Procedure to delegate the invocation to.
     * @param function The Function that will create the input for the delegate
     * @return A new Procedure
     */
    public static <T1, T2> Procedure<T1> bind(
            Procedure<? super T2> delegate,
            Function<? super T1, T2> function)
    {
        return new BindProcedure<T1, T2>(delegate, function);
    }

    /**
     * Bind the input of a ObjectIntProcedure to the result of an function, returning a new ObjectIntProcedure.
     *
     * @param delegate The ObjectIntProcedure to delegate the invocation to.
     * @param function The Function that will create the input for the delegate
     * @return A new ObjectIntProcedure
     */
    public static <T1, T2> ObjectIntProcedure<T1> bind(
            ObjectIntProcedure<? super T2> delegate,
            Function<? super T1, T2> function)
    {
        return new BindObjectIntProcedure<T1, T2>(delegate, function);
    }

    /**
     * Bind the input of the first argument of a Procedure2 to the result of an function, returning a new Procedure2.
     *
     * @param delegate The Procedure2 to delegate the invocation to.
     * @param function The Function that will create the input for the delegate
     * @return A new Procedure2
     */
    public static <T1, T2, T3> Procedure2<T1, T3> bind(
            Procedure2<? super T2, T3> delegate, Function<? super T1, T2> function)
    {
        return new BindProcedure2<T1, T2, T3>(delegate, function);
    }

    public static Function<Integer, Integer> squaredInteger()
    {
        return SQUARED_INTEGER;
    }

    public static <T, V> Function<T, V> firstNotNullValue(Function<T, V>... functions)
    {
        return new FirstNotNullFunction<T, V>(functions);
    }

    public static <T> Function<T, String> firstNotEmptyStringValue(
            Function<T, String>... functions)
    {
        return new FirstNotEmptyStringFunction<T>(functions);
    }

    public static <T1, T2, I extends Iterable<T2>> Function<T1, I> firstNotEmptyCollectionValue(
            Function<T1, I>... functions)
    {
        return new FirstNotEmptyCollectionFunction<T1, T2, I>(functions);
    }

    public static <T, V> Function<T, V> ifTrue(
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function)
    {
        return new IfFunction<T, V>(predicate, function);
    }

    public static <T, V> Function<T, V> ifElse(
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> trueFunction,
            Function<? super T, ? extends V> falseFunction)
    {
        return new IfFunction<T, V>(predicate, trueFunction, falseFunction);
    }

    public static <T extends Comparable<? super T>, V> CaseFunction<T, V> caseDefault(
            Function<? super T, ? extends V> defaultFunction)
    {
        return new CaseFunction<T, V>(defaultFunction);
    }

    public static <T extends Comparable<? super T>, V> CaseFunction<T, V> caseDefault(
            Function<? super T, ? extends V> defaultFunction,
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function)
    {
        CaseFunction<T, V> caseFunction = Functions.caseDefault(defaultFunction);
        return caseFunction.addCase(predicate, function);
    }

    public static <T, V> Function<T, V> synchronizedEach(Function<T, V> function)
    {
        return new SynchronizedFunction<T, V>(function);
    }

    public static Function<String, Class<?>> classForName()
    {
        return CLASS_FOR_NAME;
    }

    private static final class FirstNotNullFunction<T, V> implements Function<T, V>
    {
        private static final long serialVersionUID = 1L;

        private final Function<T, V>[] functions;

        private FirstNotNullFunction(Function<T, V>... functions)
        {
            this.functions = functions;
        }

        public V valueOf(T object)
        {
            for (Function<T, V> function : this.functions)
            {
                V result = function.valueOf(object);
                if (result != null)
                {
                    return result;
                }
            }
            return null;
        }
    }

    private static final class FirstNotEmptyStringFunction<T> implements Function<T, String>
    {
        private static final long serialVersionUID = 1L;

        private final Function<T, String>[] functions;

        private FirstNotEmptyStringFunction(Function<T, String>... functions)
        {
            this.functions = functions;
        }

        public String valueOf(T object)
        {
            for (Function<T, String> function : this.functions)
            {
                String result = function.valueOf(object);
                if (StringIterate.notEmpty(result))
                {
                    return result;
                }
            }
            return null;
        }
    }

    private static final class FirstNotEmptyCollectionFunction<T1, T2, I extends Iterable<T2>> implements Function<T1, I>
    {
        private static final long serialVersionUID = 1L;

        private final Function<T1, I>[] functions;

        private FirstNotEmptyCollectionFunction(Function<T1, I>[] functions)
        {
            this.functions = functions;
        }

        public I valueOf(T1 object)
        {
            for (Function<T1, I> function : this.functions)
            {
                I result = function.valueOf(object);
                if (Iterate.notEmpty(result))
                {
                    return result;
                }
            }
            return null;
        }
    }

    private static final class SynchronizedFunction<T, V> implements Function<T, V>
    {
        private static final long serialVersionUID = 1L;

        private final Function<T, V> function;

        private SynchronizedFunction(Function<T, V> function)
        {
            this.function = function;
        }

        public V valueOf(T each)
        {
            synchronized (each)
            {
                return this.function.valueOf(each);
            }
        }
    }

    public static <T1, T2, T3> FunctionChain<T1, T2, T3> chain(Function<T1, T2> function1, Function<? super T2, T3> function2)
    {
        return new FunctionChain<T1, T2, T3>(function1, function2);
    }

    public static <T1, T2> BooleanFunctionChain<T1, T2> chainBoolean(Function<T1, T2> function1, BooleanFunction<? super T2> function2)
    {
        return new BooleanFunctionChain<T1, T2>(function1, function2);
    }

    public static <T1, T2> ByteFunctionChain<T1, T2> chainByte(Function<T1, T2> function1, ByteFunction<? super T2> function2)
    {
        return new ByteFunctionChain<T1, T2>(function1, function2);
    }

    public static <T1, T2> CharFunctionChain<T1, T2> chainChar(Function<T1, T2> function1, CharFunction<? super T2> function2)
    {
        return new CharFunctionChain<T1, T2>(function1, function2);
    }

    public static <T1, T2> DoubleFunctionChain<T1, T2> chainDouble(Function<T1, T2> function1, DoubleFunction<? super T2> function2)
    {
        return new DoubleFunctionChain<T1, T2>(function1, function2);
    }

    public static <T1, T2> FloatFunctionChain<T1, T2> chainFloat(Function<T1, T2> function1, FloatFunction<? super T2> function2)
    {
        return new FloatFunctionChain<T1, T2>(function1, function2);
    }

    public static <T1, T2> IntFunctionChain<T1, T2> chainInt(Function<T1, T2> function1, IntFunction<? super T2> function2)
    {
        return new IntFunctionChain<T1, T2>(function1, function2);
    }

    public static <T1, T2> LongFunctionChain<T1, T2> chainLong(Function<T1, T2> function1, LongFunction<? super T2> function2)
    {
        return new LongFunctionChain<T1, T2>(function1, function2);
    }

    public static <T1, T2> ShortFunctionChain<T1, T2> chainShort(Function<T1, T2> function1, ShortFunction<? super T2> function2)
    {
        return new ShortFunctionChain<T1, T2>(function1, function2);
    }

    private static class DoublePassThruFunction implements Function<Double, Double>, DoubleFunction<Double>
    {
        private static final long serialVersionUID = 1L;

        public double doubleValueOf(Double each)
        {
            return each.doubleValue();
        }

        public Double valueOf(Double each)
        {
            return each;
        }

        @Override
        public String toString()
        {
            return DoublePassThruFunction.class.getSimpleName();
        }
    }

    private static class IntegerPassThruFunction implements Function<Integer, Integer>, IntFunction<Integer>
    {
        private static final long serialVersionUID = 1L;

        public int intValueOf(Integer each)
        {
            return each.intValue();
        }

        public Integer valueOf(Integer each)
        {
            return each;
        }

        @Override
        public String toString()
        {
            return IntegerPassThruFunction.class.getSimpleName();
        }
    }

    private static class LongPassThruFunction implements Function<Long, Long>, LongFunction<Long>
    {
        private static final long serialVersionUID = 1L;

        public long longValueOf(Long each)
        {
            return each.longValue();
        }

        public Long valueOf(Long each)
        {
            return each;
        }

        @Override
        public String toString()
        {
            return LongPassThruFunction.class.getSimpleName();
        }
    }

    private static final class DefaultFunction<T, V> implements Function<T, V>
    {
        private static final long serialVersionUID = 1L;
        private final Function<? super T, ? extends V> function;
        private final V defaultValue;

        private DefaultFunction(Function<? super T, ? extends V> newFunction, V newDefaultValue)
        {
            this.function = newFunction;
            this.defaultValue = newDefaultValue;
        }

        public V valueOf(T anObject)
        {
            V returnValue = this.function.valueOf(anObject);
            if (returnValue == null)
            {
                return this.defaultValue;
            }
            return returnValue;
        }
    }

    private static final class NullSafeFunction<T, V> implements Function<T, V>
    {
        private static final long serialVersionUID = 1L;
        private final Function<? super T, ? extends V> function;
        private final V nullValue;

        private NullSafeFunction(Function<? super T, ? extends V> function, V nullValue)
        {
            this.function = function;
            this.nullValue = nullValue;
        }

        public V valueOf(T object)
        {
            return object == null ? this.nullValue : this.function.valueOf(object);
        }
    }

    public static <T, V1, V2> Function<T, Pair<V1, V2>> pair(
            final Function<? super T, V1> function1,
            final Function<? super T, V2> function2)
    {
        return new Function<T, Pair<V1, V2>>()
        {
            public Pair<V1, V2> valueOf(T t)
            {
                return Tuples.pair(function1.valueOf(t), function2.valueOf(t));
            }
        };
    }

    /**
     * @return A function that gets the key out of a {@link java.util.Map.Entry}
     */
    @SuppressWarnings("UnnecessaryFullyQualifiedName")
    public static <K> Function<Map.Entry<K, ?>, K> getKeyFunction()
    {
        return (Function<Map.Entry<K, ?>, K>) MAP_KEY_FUNCTION;
    }

    /**
     * @return A function that gets the value out of a {@link java.util.Map.Entry}
     */
    @SuppressWarnings("UnnecessaryFullyQualifiedName")
    public static <V> Function<Map.Entry<?, V>, V> getValueFunction()
    {
        return (Function<Map.Entry<?, V>, V>) MAP_VALUE_FUNCTION;
    }

    private static class MapKeyFunction<K> implements Function<Map.Entry<K, ?>, K>
    {
        private static final long serialVersionUID = 1L;

        public K valueOf(Map.Entry<K, ?> entry)
        {
            return entry.getKey();
        }
    }

    private static class MapValueFunction<V> implements Function<Map.Entry<?, V>, V>
    {
        private static final long serialVersionUID = 1L;

        public V valueOf(Map.Entry<?, V> entry)
        {
            return entry.getValue();
        }
    }

    /**
     * @return A function that gets the size of an {@code Iterable}
     */
    public static Function<Iterable<?>, Integer> getSizeOf()
    {
        return SIZE_FUNCTION;
    }

    public static class SizeFunction extends IntegerFunctionImpl<Iterable<?>>
    {
        private static final long serialVersionUID = 1L;

        public int intValueOf(Iterable<?> iterable)
        {
            return Iterate.sizeOf(iterable);
        }
    }

    public static final class FunctionChain<T1, T2, T3> implements Function<T1, T3>
    {
        private static final long serialVersionUID = 1L;
        private final Function<T1, T2> function1;
        private final Function<? super T2, T3> function2;

        private FunctionChain(Function<T1, T2> function1, Function<? super T2, T3> function2)
        {
            this.function1 = function1;
            this.function2 = function2;
        }

        public T3 valueOf(T1 object)
        {
            return this.function2.valueOf(this.function1.valueOf(object));
        }

        public <T4> FunctionChain<T1, T3, T4> chain(Function<? super T3, T4> function)
        {
            return new FunctionChain<T1, T3, T4>(this, function);
        }

        public BooleanFunctionChain<T1, T3> chainBoolean(BooleanFunction<? super T3> function)
        {
            return new BooleanFunctionChain<T1, T3>(this, function);
        }

        public ByteFunctionChain<T1, T3> chainByte(ByteFunction<? super T3> function)
        {
            return new ByteFunctionChain<T1, T3>(this, function);
        }

        public CharFunctionChain<T1, T3> chainChar(CharFunction<? super T3> function)
        {
            return new CharFunctionChain<T1, T3>(this, function);
        }

        public DoubleFunctionChain<T1, T3> chainDouble(DoubleFunction<? super T3> function)
        {
            return new DoubleFunctionChain<T1, T3>(this, function);
        }

        public FloatFunctionChain<T1, T3> chainFloat(FloatFunction<? super T3> function)
        {
            return new FloatFunctionChain<T1, T3>(this, function);
        }

        public IntFunctionChain<T1, T3> chainInt(IntFunction<? super T3> function)
        {
            return new IntFunctionChain<T1, T3>(this, function);
        }

        public LongFunctionChain<T1, T3> chainLong(LongFunction<? super T3> function)
        {
            return new LongFunctionChain<T1, T3>(this, function);
        }

        public ShortFunctionChain<T1, T3> chainShort(ShortFunction<? super T3> function)
        {
            return new ShortFunctionChain<T1, T3>(this, function);
        }
    }

    public static final class BooleanFunctionChain<T1, T2> implements BooleanFunction<T1>
    {
        private static final long serialVersionUID = 1L;
        private final Function<T1, T2> function1;
        private final BooleanFunction<? super T2> function2;

        private BooleanFunctionChain(Function<T1, T2> function1, BooleanFunction<? super T2> function2)
        {
            this.function1 = function1;
            this.function2 = function2;
        }

        public boolean booleanValueOf(T1 object)
        {
            return this.function2.booleanValueOf(this.function1.valueOf(object));
        }
    }

    public static final class ByteFunctionChain<T1, T2> implements ByteFunction<T1>
    {
        private static final long serialVersionUID = 1L;
        private final Function<T1, T2> function1;
        private final ByteFunction<? super T2> function2;

        private ByteFunctionChain(Function<T1, T2> function1, ByteFunction<? super T2> function2)
        {
            this.function1 = function1;
            this.function2 = function2;
        }

        public byte byteValueOf(T1 object)
        {
            return this.function2.byteValueOf(this.function1.valueOf(object));
        }
    }

    public static final class CharFunctionChain<T1, T2> implements CharFunction<T1>
    {
        private static final long serialVersionUID = 1L;
        private final Function<T1, T2> function1;
        private final CharFunction<? super T2> function2;

        private CharFunctionChain(Function<T1, T2> function1, CharFunction<? super T2> function2)
        {
            this.function1 = function1;
            this.function2 = function2;
        }

        public char charValueOf(T1 object)
        {
            return this.function2.charValueOf(this.function1.valueOf(object));
        }
    }

    public static final class DoubleFunctionChain<T1, T2> implements DoubleFunction<T1>
    {
        private static final long serialVersionUID = 1L;
        private final Function<T1, T2> function1;
        private final DoubleFunction<? super T2> function2;

        private DoubleFunctionChain(Function<T1, T2> function1, DoubleFunction<? super T2> function2)
        {
            this.function1 = function1;
            this.function2 = function2;
        }

        public double doubleValueOf(T1 object)
        {
            return this.function2.doubleValueOf(this.function1.valueOf(object));
        }
    }

    public static final class FloatFunctionChain<T1, T2> implements FloatFunction<T1>
    {
        private static final long serialVersionUID = 1L;
        private final Function<T1, T2> function1;
        private final FloatFunction<? super T2> function2;

        private FloatFunctionChain(Function<T1, T2> function1, FloatFunction<? super T2> function2)
        {
            this.function1 = function1;
            this.function2 = function2;
        }

        public float floatValueOf(T1 object)
        {
            return this.function2.floatValueOf(this.function1.valueOf(object));
        }
    }

    public static final class IntFunctionChain<T1, T2> implements IntFunction<T1>
    {
        private static final long serialVersionUID = 1L;
        private final Function<T1, T2> function1;
        private final IntFunction<? super T2> function2;

        private IntFunctionChain(Function<T1, T2> function1, IntFunction<? super T2> function2)
        {
            this.function1 = function1;
            this.function2 = function2;
        }

        public int intValueOf(T1 object)
        {
            return this.function2.intValueOf(this.function1.valueOf(object));
        }
    }

    public static final class LongFunctionChain<T1, T2> implements LongFunction<T1>
    {
        private static final long serialVersionUID = 1L;
        private final Function<T1, T2> function1;
        private final LongFunction<? super T2> function2;

        private LongFunctionChain(Function<T1, T2> function1, LongFunction<? super T2> function2)
        {
            this.function1 = function1;
            this.function2 = function2;
        }

        public long longValueOf(T1 object)
        {
            return this.function2.longValueOf(this.function1.valueOf(object));
        }
    }

    public static final class ShortFunctionChain<T1, T2> implements ShortFunction<T1>
    {
        private static final long serialVersionUID = 1L;
        private final Function<T1, T2> function1;
        private final ShortFunction<? super T2> function2;

        private ShortFunctionChain(Function<T1, T2> function1, ShortFunction<? super T2> function2)
        {
            this.function1 = function1;
            this.function2 = function2;
        }

        public short shortValueOf(T1 object)
        {
            return this.function2.shortValueOf(this.function1.valueOf(object));
        }
    }

    private static class FirstOfPairFunction<T> implements Function<Pair<T, ?>, T>
    {
        private static final long serialVersionUID = 1L;

        public T valueOf(Pair<T, ?> pair)
        {
            return pair.getOne();
        }
    }

    private static class SecondOfPairFunction<T> implements Function<Pair<?, T>, T>
    {
        private static final long serialVersionUID = 1L;

        public T valueOf(Pair<?, T> pair)
        {
            return pair.getTwo();
        }
    }

    private static class ClassForNameFunction extends CheckedFunction<String, Class<?>>
    {
        private static final long serialVersionUID = 1L;

        public Class<?> safeValueOf(String className) throws ClassNotFoundException
        {
            return Class.forName(className);
        }
    }

    private static final class BindObjectIntProcedure<T1, T2> implements ObjectIntProcedure<T1>
    {
        private static final long serialVersionUID = 1L;
        private final ObjectIntProcedure<? super T2> delegate;
        private final Function<? super T1, T2> function;

        private BindObjectIntProcedure(ObjectIntProcedure<? super T2> delegate, Function<? super T1, T2> function)
        {
            this.delegate = delegate;
            this.function = function;
        }

        public void value(T1 each, int index)
        {
            this.delegate.value(this.function.valueOf(each), index);
        }
    }

    private static final class BindProcedure<T1, T2> implements Procedure<T1>
    {
        private static final long serialVersionUID = 1L;
        private final Procedure<? super T2> delegate;
        private final Function<? super T1, T2> function;

        private BindProcedure(Procedure<? super T2> delegate, Function<? super T1, T2> function)
        {
            this.delegate = delegate;
            this.function = function;
        }

        public void value(T1 each)
        {
            this.delegate.value(this.function.valueOf(each));
        }
    }

    private static final class BindProcedure2<T1, T2, T3> implements Procedure2<T1, T3>
    {
        private static final long serialVersionUID = 1L;
        private final Procedure2<? super T2, T3> delegate;
        private final Function<? super T1, T2> function;

        private BindProcedure2(Procedure2<? super T2, T3> delegate, Function<? super T1, T2> function)
        {
            this.delegate = delegate;
            this.function = function;
        }

        public void value(T1 each, T3 constant)
        {
            this.delegate.value(this.function.valueOf(each), constant);
        }
    }

    private static final class BindFunction2<T1, T2, T3> implements Function<T1, T3>
    {
        private static final long serialVersionUID = 1L;
        private final Function2<? super T1, ? super T2, ? extends T3> delegate;
        private final T2 parameter;

        private BindFunction2(Function2<? super T1, ? super T2, ? extends T3> delegate, T2 parameter)
        {
            this.delegate = delegate;
            this.parameter = parameter;
        }

        public T3 valueOf(T1 object)
        {
            return this.delegate.value(object, this.parameter);
        }
    }

    private static class SwappedPairFunction<S, T> implements Function<Pair<S, T>, Pair<T, S>>
    {
        private static final long serialVersionUID = 1L;

        public Pair<T, S> valueOf(Pair<S, T> pair)
        {
            return pair.swap();
        }
    }

    private static class ThrowingFunctionAdapter<T, V> extends CheckedFunction<T, V>
    {
        private static final long serialVersionUID = 1L;
        private final ThrowingFunction<T, V> throwingFunction;

        public ThrowingFunctionAdapter(ThrowingFunction<T, V> throwingFunction)
        {
            this.throwingFunction = throwingFunction;
        }

        public V safeValueOf(T object) throws Exception
        {
            return this.throwingFunction.safeValueOf(object);
        }
    }

    private static class TrueFunction implements Function<Object, Boolean>
    {
        private static final long serialVersionUID = 1L;

        public Boolean valueOf(Object object)
        {
            return Boolean.TRUE;
        }
    }

    private static class FalseFunction implements Function<Object, Boolean>
    {
        private static final long serialVersionUID = 1L;

        public Boolean valueOf(Object object)
        {
            return Boolean.FALSE;
        }
    }
}
