/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package babelfy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.VCARD;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

/**
 *
 * @author arthu
 */
public class ManipuladorArquivos {

    public void criaAnotacoes() {
        HttpClient httpclient = HttpClients.createDefault();
        String text;
        String lang = "en";
        String key = "a4703893-86de-4fa3-9a81-bf82c5909116";
        List<String> tweetsId = new ArrayList<>();

        BufferedReader br;

        String anotacaoes = "";
        File arquivo;
        URI uri;
        try {
            BufferedWriter buffW;
            //le arq de tweets
            br = new BufferedReader(new FileReader("src/main/resources/teste.json"));
            while (br.ready()) {

                text = br.readLine();
                System.out.println(text.split("\t")[1]);
                uri = new URIBuilder()
                        .setScheme("https")
                        .setHost("babelfy.io/")
                        .setPath("v1/disambiguate")
                        .addParameter("text", text.split("\t")[1])
                        .addParameter("lang", lang)
                        .addParameter("key", key)
                        .build();
                HttpGet httpget = new HttpGet(uri);
                httpget.addHeader("Accept", "application/json");

                CloseableHttpResponse response = (CloseableHttpResponse) httpclient.execute(httpget);
                HttpEntity entity = response.getEntity();

                //Cria arquivos para gravar anotações
                arquivo = new File("src/main/resources/Anotações/tweet - " + text.split("\t")[0] + ".json");
                if (!arquivo.exists()) {
                    arquivo.createNewFile();
                }

                String resposta = EntityUtils.toString(entity, "UTF-8");
                JSONArray jsonA = new JSONArray(resposta);
                for (Object object : jsonA) {
                    JSONObject json = (JSONObject) object;
                    json.put("tweet", text.split("\t")[0]);
                }
                anotacaoes = jsonA.toString();

                buffW = new BufferedWriter(new FileWriter(arquivo));
                buffW.write(anotacaoes);
                buffW.close();
                EntityUtils.consume(entity);
            }

            br.close();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(ManipuladorArquivos.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void criaAnotacoesIdentado() {
        HttpClient httpclient = HttpClients.createDefault();
        String text;
        String lang = "en";
        String key = "a4703893-86de-4fa3-9a81-bf82c5909116";
        List<String> tweetsId = new ArrayList<>();

        BufferedReader br;

        String anotacaoes = "";
        File arquivo;
        URI uri;
        try {
            BufferedWriter buffW;
            //le arq de tweets
            br = new BufferedReader(new FileReader("src/main/resources/teste.json"));
            while (br.ready()) {

                text = br.readLine();
                System.out.println(text.split("\t")[1]);
                uri = new URIBuilder()
                        .setScheme("https")
                        .setHost("babelfy.io/")
                        .setPath("v1/disambiguate")
                        .addParameter("text", text.split("\t")[1])
                        .addParameter("lang", lang)
                        .addParameter("key", key)
                        .build();
                HttpGet httpget = new HttpGet(uri);
                httpget.addHeader("Accept", "application/json");

                CloseableHttpResponse response = (CloseableHttpResponse) httpclient.execute(httpget);
                HttpEntity entity = response.getEntity();

                //Cria arquivos para gravar anotações
                arquivo = new File("src/main/resources/Anotações/Identado/tweet - " + text.split("\t")[0] + ".json");
                if (!arquivo.exists()) {
                    arquivo.createNewFile();
                }

                String resposta = EntityUtils.toString(entity, "UTF-8");
                JSONArray jsonA = new JSONArray(resposta);
                for (Object object : jsonA) {
                    JSONObject json = (JSONObject) object;
                    json.put("tweet", text.split("\t")[0]);
                }
                anotacaoes = jsonA.toString(1);

                buffW = new BufferedWriter(new FileWriter(arquivo));
                buffW.write(anotacaoes);
                buffW.close();
                EntityUtils.consume(entity);
            }

            br.close();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(ManipuladorArquivos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void criaOpenAnnotations() {
        File dir = new File("src/main/resources/Anotações");
        String[] anotacoes = dir.list();

        String openAnnotation;
        File arquivo;
        BufferedWriter buffW;
        BufferedReader br;
        for (String a : anotacoes) {
            try {
                File arquivo2 = new File("src/main/resources/Anotações/" + a);
                if (!arquivo2.isDirectory()) {
                    br = new BufferedReader(new FileReader(arquivo2));
                    JSONArray arrAnotacao = new JSONArray(br.readLine());
                    for (Object object : arrAnotacao) {
                            JSONObject json = (JSONObject) object;

                            openAnnotation = "<http://example.org/anotação> a <oa:Annotation> ;\n"
                                    + "    <oa:motivatedBy> <oa:commenting> ;\n"
                                    + "    <dcterms:creator> <Babelfy> ;\n"
                                    + "    <dcterms:created> \"<dataCriacao>\"^^xsd:datetime ;\n";

                            if (json.getString("DBpediaURL").equals("")) {
                                openAnnotation += "    <oa:hasBody> _:body. " + json.getString("BabelNetURL") + "\n";
                            } else if (json.getString("BabelNetURL").equals("")) {
                                openAnnotation += "    <oa:hasBody> _:body. " + json.getString("DBpediaURL") + "\n";

                            } else {
                                openAnnotation += "    <oa:hasBody> _:body. \n"
                                        + "_:body    <oa:Choice> _:choice. ;\n"
                                        + "_:choice    <oa:items> _:items. :\n"
                                        + "_:items    <" + json.getString("DBpediaURL") + " " + json.getString("BabelNetURL") + "> \n";
                            }
                            openAnnotation += "    <oa:hasTarget> _:target.\n"
                                    + "_:target    <oa:hasSource> <http://twitter.com/" + json.get("tweet") + "> ;\n"
                                    + "    <oa:hasSelector> _:selector.\n"
                                    + "_:selector    a <oa:TextPositionSelector> ;\n"
                                    
                                  
                                    + "    <oa:start> \"" + json.getJSONObject("charFragment").getInt("start") + "\"^^xsd:int  ;\n"
                                    + "    <oa:end> \"" + (json.getJSONObject("charFragment").getInt("end")) + "\"^^xsd:int.";

                            arquivo = new File("src/main/resources/Anotações/OpenAnnotations/" + json.get("tweet") + "-" + json.getString("babelSynsetID").replace(":", ""));
                            if (!arquivo.exists()) {
                                arquivo.createNewFile();
                            }
                            buffW = new BufferedWriter(new FileWriter(arquivo));
                            buffW.write(openAnnotation);
                            buffW.close();
                    }
                }
            } catch (JSONException e) {
                System.out.println("Não é json");
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ManipuladorArquivos.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ManipuladorArquivos.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    void criaAnotacoesRdf() {
        File dir = new File("src/main/resources/Anotações");
        String[] anotacoes = dir.list();

        BufferedReader br;

        for (String a : anotacoes) {
            File arquivo2 = new File("src/main/resources/Anotações/" + a);
            if (!arquivo2.isDirectory()) {
                try {
                    br = new BufferedReader(new FileReader(arquivo2));
                    JSONObject anotacao = new JSONObject(br.readLine());
                    if (anotacao.has("Resources")) {
                        for (Object object : anotacao.getJSONArray("Resources")) {
                            JSONObject json = (JSONObject) object;
                            Model model = ModelFactory.createDefaultModel();
                            String anotacaoURI = json.getString("@URI");
                            model.createResource(anotacaoURI);
                            String[] tipos = json.getString("@types").split(",");
                            for (String tipo : tipos) {
                                model.getResource(anotacaoURI).addProperty(RDF.type, tipo);
                            }

                            model.write(new BufferedWriter(new FileWriter("src/main/resources/Anotações/RDFs/" + json.getString("@URI")
                                    .replace("http://", "")
                                    .replace("/", "-")
                                    .replace(".", "-")
                                    + ".rdf")), "RDF/XML-ABBREV");
                        }
                    }
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(ManipuladorArquivos.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(ManipuladorArquivos.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    void criaAnotacoesNTriplets() {
        File dir = new File("src/main/resources/Anotações");
        String[] anotacoes = dir.list();

        BufferedReader br;

        for (String a : anotacoes) {
            File arquivo2 = new File("src/main/resources/Anotações/" + a);
            if (!arquivo2.isDirectory()) {
                try {
                    br = new BufferedReader(new FileReader(arquivo2));
                    JSONObject anotacao = new JSONObject(br.readLine());
                    if (anotacao.has("Resources")) {
                        for (Object object : anotacao.getJSONArray("Resources")) {
                            JSONObject json = (JSONObject) object;
                            Model model = ModelFactory.createDefaultModel();
                            String anotacaoURI = json.getString("@URI");
                            model.createResource(anotacaoURI);
                            String[] tipos = json.getString("@types").split(",");
                            for (String tipo : tipos) {
                                model.getResource(anotacaoURI).addProperty(RDF.type, tipo);
                            }

                            model.write(new BufferedWriter(new FileWriter("src/main/resources/Anotações/N-Triplets/" + json.getString("@URI")
                                    .replace("http://", "")
                                    .replace("/", "-")
                                    .replace(".", "-")
                                    + ".ntriplet")), "N-TRIPLES");
                        }
                    }
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(ManipuladorArquivos.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(ManipuladorArquivos.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
