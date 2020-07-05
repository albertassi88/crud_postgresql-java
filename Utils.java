package postgre_sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;

public class Utils {

	static Scanner sc = new Scanner(System.in);

	public static Connection conectar() {
		Properties props = new Properties();
		props.setProperty("user", "ruben");
		props.setProperty("password", "rlda88fm");
		props.setProperty("ssl", "false");
		String url_servidor = "jdbc:postgresql://localhost:5432/jpostgresql";

		try {
			return DriverManager.getConnection(url_servidor, props);
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof ClassNotFoundException) {
				System.err.println("Verifique o driver de conexão.");
			} else {
				System.err.println("Verifique se o servidor esta ativo.");
			}
			System.exit(-42);
			return null;
		}
	}

	public static void desconectar(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void listar() {
		String buscar_todos = "SELECT * FROM produtos";

		try {
			Connection conn = conectar();
			PreparedStatement produtos = conn.prepareStatement(buscar_todos, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			ResultSet res = produtos.executeQuery();
			res.last();
			int qtd = res.getRow();
			res.beforeFirst();

			if (qtd > 0) {
				System.out.println("Listando produtos....");
				System.out.println(".....................");
				while (res.next()) {
					System.out.println("ID: " + res.getInt(1));
					System.out.println("Produto: " + res.getString(2));
					System.out.println("Preço: " + res.getFloat(3));
					System.out.println("Estoque: " + res.getInt(4));
					System.out.println(".....................");
				}
			} else {
				System.out.println("Não existe produtos cadastrados.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Erro em buscar todos os produtos.");
			System.exit(-42);
		}
	}

	public static void inserir() {
		System.out.println("Informe o nome do produto: ");
		String nome = sc.nextLine();

		System.out.println("Informe o preço do produto: ");
		float preco = sc.nextFloat();

		System.out.println("Informe a quantidade em estoque: ");
		int estoque = sc.nextInt();

		String inserir = "INSERT INTO produtos (nome, preco, estoque) VALUES (?, ?, ?)";

		try {
			Connection conn = conectar();
			PreparedStatement pres = conn.prepareStatement(inserir);

			pres.setString(1, nome);
			pres.setFloat(2, preco);
			pres.setInt(3, estoque);
			pres.executeUpdate();
			pres.close();
			desconectar(conn);
			System.out.println("O produto " + nome + " foi inserido com sucesso.");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Erro em inserir o produto.");
			System.exit(-42);
		}
	}

	public static void atualizar() {
		System.out.println("Informe o código do produto:");
		int id = Integer.parseInt(sc.nextLine());

		String buscar = "SELECT * FROM produtos WHERE id=?";

		try {
			Connection conn = conectar();
			PreparedStatement produto = conn.prepareStatement(buscar, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			produto.setInt(1, id);
			ResultSet res = produto.executeQuery();
			res.last();
			int qtd = res.getRow();
			res.beforeFirst();

			if (qtd > 0) {
				System.out.println("Informe o nome do produto:");
				String nome = sc.nextLine();

				System.out.println("Informe o preço do produto:");
				float preco = sc.nextFloat();

				System.out.println("Informe a quantidade em estoque:");
				int estoque = sc.nextInt();

				String atualizar = "UPDATE produtos SET nome=?, preco=?, estoque=? WHERE id=?";
				PreparedStatement upd = conn.prepareStatement(atualizar);
				upd.setString(1, nome);
				upd.setFloat(2, preco);
				upd.setInt(3, estoque);
				upd.setInt(4, id);
				upd.executeUpdate();
				upd.close();
				desconectar(conn);
				System.out.println("O produto " + nome + " foi atualizado com sucesso.");
			} else {
				System.out.println("Não exite produto com o id informado.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Não foi possível atualizar o produto.");
			System.exit(-42);
		}
	}

	public static void deletar() {
		String deletar = "DELETE FROM produtos WHERE id=?";
		String buscar = "SELECT * FROM produtos WHERE id=?";

		System.out.println("Informe o código do produto:");
		int id = Integer.parseInt(sc.nextLine());

		try {
			Connection conn = conectar();
			PreparedStatement produto = conn.prepareStatement(buscar, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			produto.setInt(1, id);
			ResultSet res = produto.executeQuery();
			res.last();
			int qtd = res.getRow();
			res.beforeFirst();

			if (qtd > 0) {
				PreparedStatement del = conn.prepareStatement(deletar);
				del.setInt(1, id);
				del.executeUpdate();
				del.close();
				desconectar(conn);
				System.out.println("O produto foi deletado com sucesso.");
			} else {
				System.out.println("Não exite produto com o id informado.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Erro ao deletar o produto.");
			System.exit(-42);
		}
	}

	public static void menu() {
		System.out.println("==================Gerenciamento de Produtos===============");
		System.out.println("Selecione uma opção: ");
		System.out.println("1 - Listar produtos.");
		System.out.println("2 - Inserir produtos.");
		System.out.println("3 - Atualizar produtos.");
		System.out.println("4 - Deletar produtos.");

		int opcao = Integer.parseInt(sc.nextLine());
		if (opcao == 1) {
			listar();
		} else if (opcao == 2) {
			inserir();
		} else if (opcao == 3) {
			atualizar();
		} else if (opcao == 4) {
			deletar();
		} else {
			System.out.println("Opção inválida.");
		}
	}
}
