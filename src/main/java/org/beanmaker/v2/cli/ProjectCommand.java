package org.beanmaker.v2.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

@Command(name = "project", description = "Manage project parameters")
class ProjectCommand implements Callable<Integer> {

    @Option(names = { "-n", "--name" }, paramLabel = "<name>", description = "change project name")
    String name;

    @Option(names = "--description", paramLabel = "<description>", description = "set short description of the project")
    String description;

    @Option(names = { "--db", "--database"}, paramLabel = "<database code>", description = "specify database to connect to")
    String database;

    @Option(names = "--default-package", paramLabel = "<package name>", description = "set default package for generated beans")
    String defaultPackage;

    @Option(names = "--gen-source-dir", paramLabel = "<path to source dir>", description = "set where to save generated source files")
    String genSourceDir;


    @Override
    public Integer call() {
        // TODO: vérifier si beanmaker.xml existe dans le répertoire, si non = erreur, il faut d'abord utiliser la commande init pour créer le fichier

        // TODO: charger la configuration en mémoire

        // TODO: parcourir les options :
        //   TODO: si aucune option passée, notifier et quitter (0)
        //   TODO: pour chaque option, effectuer le changement de configuration si la valeur est différente de la valeur actuelle (noter l'existence d'une différence)
        //   TODO: s'il y a des différences, enregistrer le nouveau fichier beanmaker.xml, signaler qu'il a été mis à jour et quitter
        //   TODO: si pas de différence dans les options, notifier la chose et quitter

        return ReturnCode.SUCCESS.code();
    }

}
