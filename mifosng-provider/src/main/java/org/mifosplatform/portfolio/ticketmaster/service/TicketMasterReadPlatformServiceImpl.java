package org.mifosplatform.portfolio.ticketmaster.service;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.ticketmaster.data.ClientTicketData;
import org.mifosplatform.portfolio.ticketmaster.data.ProblemsData;
import org.mifosplatform.portfolio.ticketmaster.data.TicketMasterData;
import org.mifosplatform.portfolio.ticketmaster.data.UsersData;
import org.mifosplatform.portfolio.ticketmaster.domain.PriorityType;
import org.mifosplatform.portfolio.ticketmaster.domain.PriorityTypeEnum;
import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class TicketMasterReadPlatformServiceImpl  implements TicketMasterReadPlatformService{
	
	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;

	@Autowired
	public TicketMasterReadPlatformServiceImpl(final PlatformSecurityContext context,
			final TenantAwareRoutingDataSource dataSource) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<TicketMasterData> retrieveTicketStatusData() {
		context.authenticatedUser();

		statusMapper mapper = new statusMapper();

		String sql = "select " + mapper.schema()+" where s.status_type='open';";

		return this.jdbcTemplate.query(sql, mapper, new Object[] {});
	}

	private static final class statusMapper implements
			RowMapper<TicketMasterData> {

		public String schema() {
			return "s.id as id,s.status_code as statusCode,s.status_description as statusDesc from ticket_status s ";

		}

		@Override
		public TicketMasterData mapRow(ResultSet rs, int rowNum)
				throws SQLException {

			Long id = rs.getLong("id");
			String statusCode = rs.getString("statusCode");
			String statusDesc = rs.getString("statusDesc");
	
			TicketMasterData data = new TicketMasterData(id, statusCode,statusDesc);
			return data;

		}

	}


	@Override
	public List<ProblemsData> retrieveProblemData() {

		context.authenticatedUser();

		DataMapper mapper = new DataMapper();

		String sql = "select " + mapper.schema();

		return this.jdbcTemplate.query(sql, mapper, new Object[] {});
	}

	private static final class DataMapper implements
			RowMapper<ProblemsData> {

		public String schema() {
			return "p.id as id,p.problem_code as problemCode,p.problem_description as problemDescription from problems p";

		}

		@Override
		public ProblemsData mapRow(ResultSet rs, int rowNum)
				throws SQLException {

			Long id = rs.getLong("id");
			String problemCode = rs.getString("problemCode");
			String problemDescription = rs.getString("problemDescription");

			ProblemsData data = new ProblemsData(id, problemCode,problemDescription);

			return data;

		}

	}

	@Override
	public List<UsersData> retrieveUsers() {
		context.authenticatedUser();

		UserMapper mapper = new UserMapper();

		String sql = "select " + mapper.schema();

		return this.jdbcTemplate.query(sql, mapper, new Object[] {});
	}

	private static final class UserMapper implements
			RowMapper<UsersData> {

		public String schema() {
			return "u.id as id,u.username as username from m_appuser u where u.is_deleted=0";

		}

		@Override
		public UsersData mapRow(ResultSet rs, int rowNum)
				throws SQLException {

			Long id = rs.getLong("id");
			String username = rs.getString("username");

			UsersData data = new UsersData(id, username);

			return data;

		}

	}

	@Override
	public List<TicketMasterData> retrieveClientTicketDetails(Long clientId) {
		try {
			final ClientTicketMapper mapper = new ClientTicketMapper();

			final String sql = "select " + mapper.clientOrderLookupSchema()+" and a.client_id= ? ";

			return jdbcTemplate.query(sql, mapper, new Object[] { clientId});
			} catch (EmptyResultDataAccessException e) {
			return null;
			}

			}

			private static final class ClientTicketMapper implements RowMapper<TicketMasterData> {

			public String clientOrderLookupSchema() {
			return "a.id as id,a.priority as priority,a.ticket_date as ticketDate,a.assigned_to as userId,b.problem_description as problemDescription ,a.status as status,username as assignedTo ,(Select comments from ticket_details x where a.id=x.ticket_id"
				+" and x.id=(select max(id) from ticket_details y where a.id=y.ticket_id)) as LastComment "
			+"from ticket_master a, problems b, m_appuser c where a.problem_code=b.problem_code and a.assigned_to=c.id ";
			}

			@Override
			public TicketMasterData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

			final Long id = rs.getLong("id");
			final String priority = rs.getString("priority");
			final String status = rs.getString("status");
			final String LastComment = rs.getString("LastComment");
			final String problemDescription = rs.getString("problemDescription");
			final String assignedTo = rs.getString("assignedTo");
			final String usersId = rs.getString("userId");
			LocalDate ticketDate=JdbcSupport.getLocalDate(rs,"ticketDate");
			int userId=new Integer(usersId);
			return new TicketMasterData(id, priority, status, userId, ticketDate,LastComment,problemDescription,assignedTo);
			}
			}

			@Override
			public TicketMasterData retrieveSingleTicketDetails(Long clientId,Long ticketId) {
				try {
					final ClientTicketMapper mapper = new ClientTicketMapper();

					final String sql = "select " + mapper.clientOrderLookupSchema()+" and a.client_id= ? and a.id=?";

					return jdbcTemplate.queryForObject(sql, mapper, new Object[] { clientId,ticketId});
					} catch (EmptyResultDataAccessException e) {
					return null;
					}

					}

			@Override
			public List<EnumOptionData> retrievePriorityData() {
				EnumOptionData low=PriorityTypeEnum.priorityType(PriorityType.LOW);
				EnumOptionData medium=PriorityTypeEnum.priorityType(PriorityType.MEDIUM);
				EnumOptionData high=PriorityTypeEnum.priorityType(PriorityType.HIGH);
				List<EnumOptionData> priorityType=Arrays.asList(low,medium,high);
				return priorityType;
			}

			@Override
			public List<TicketMasterData> retrieveTicketCloseStatusData() {
				statusMapper mapper = new statusMapper();
				String sql = "select " + mapper.schema()+" where s.status_type='CLOSE'";

				return this.jdbcTemplate.query(sql, mapper, new Object[] {});
			}

			@Override
			public List<TicketMasterData> retrieveClientTicketHistory(
					Long ticketId) {
				context.authenticatedUser();

				TicketDataMapper mapper = new TicketDataMapper();

				String sql = "select " + mapper.schema()+"where t.ticket_id=tm.id and t.ticket_id=?";

				return this.jdbcTemplate.query(sql, mapper, new Object[] { ticketId});
			}

			private static final class TicketDataMapper implements
					RowMapper<TicketMasterData> {

				public String schema() {
					return " t.id AS id,t.created_date AS createDate,t.assigned_to AS assignedTo,t.comments as description," +
							"t.attachments AS attachments  FROM ticket_details t,ticket_master tm  ";

				}

				@Override
				public TicketMasterData mapRow(ResultSet rs, int rowNum)
						throws SQLException {

					Long id = rs.getLong("id");
					LocalDate createdDate=JdbcSupport.getLocalDate(rs,"createDate");
					String assignedTo = rs.getString("assignedTo");
					String description = rs.getString("description");
					String attachments = rs.getString("attachments");
					String fileName=null;
					if(attachments!=null){
					File file=new File(attachments);
					 fileName=file.getName();
					}
					TicketMasterData data = new TicketMasterData(id,createdDate, assignedTo,description,fileName);

					return data;

				}

			}


			@Override
			public List<ClientTicketData> retrieveAssignedTickets() {
				AppUser user = this.context.authenticatedUser();
				
				final UserTicketsMapper mapper = new UserTicketsMapper();

				final String sql = "select " + mapper.userTicketSchema()+" AND a.assigned_to= ? ";

				return jdbcTemplate.query(sql, mapper, new Object[] { user.getId() });
				
			}
			
			private static final class UserTicketsMapper implements RowMapper<ClientTicketData> {
				
				public String userTicketSchema() {
					return " a.id as id, a.priority as priority, a.status as status, a.assigned_to as userId, a.ticket_date as ticketDate, " 
						+  " (Select comments from ticket_details x where a.id=x.ticket_id and x.id=(select max(id) from ticket_details y where a.id=y.ticket_id)) as LastComment, "
						+  " b.problem_description as problemDescription , username as assignedTo , a.client_id as clientId "				
						+  " from ticket_master a, problems b, m_appuser c where a.problem_code=b.problem_code and a.assigned_to=c.id ";
					}

				@Override
				public ClientTicketData mapRow(ResultSet rs, int rowNum) throws SQLException {
					final Long id = rs.getLong("id");
					final String priority = rs.getString("priority");
					final String status = rs.getString("status");
					final Long userId = rs.getLong("userId");
					final LocalDate ticketDate=JdbcSupport.getLocalDate(rs,"ticketDate");
					final String lastComment = rs.getString("LastComment");
					final String problemDescription = rs.getString("problemDescription");
					final String assignedTo = rs.getString("assignedTo");
					final Long clientId = rs.getLong("clientId");
					
					return new ClientTicketData(id, priority, status, userId, ticketDate, lastComment, problemDescription, assignedTo, clientId);
				}
				
			}

	}


