package name.zicat.cofiguration.schema;


/**
 *
 * @param <S>
 * @param <T>
 */
public interface Schema<S, T> {

    /**
     *
     * @param s
     * @return
     * @throws Exception
     */
    T parse(S s) throws Exception;
}
