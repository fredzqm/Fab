package analyzer;

import configs.Configuration;

import java.util.Collection;

/**
 * Created by lamd on 12/7/2016.
 */
public interface IGenerator {
    void generate(ISystemModel sm, Configuration config, Collection<Job> jobs);
}