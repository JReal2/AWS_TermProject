package aws;

/*
* Cloud Computing
*
* Dynamic Resource Management Tool
* using AWS Java SDK Library
*
*/
import java.util.Iterator;
import java.util.Objects;
import java.util.Scanner;
import java.util.Collections;
import java.io.InputStream;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import com.jcraft.jsch.*;

public class awsTest {

	static AmazonEC2      ec2;

	private static void init() throws Exception {

		ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
		try {
			credentialsProvider.getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException(
					"Cannot load the credentials from the credential profiles file. " +
					"Please make sure that your credentials file is at the correct " +
					"location (~/.aws/credentials), and is in valid format.",
					e);
		}
		ec2 = AmazonEC2ClientBuilder.standard()
			.withCredentials(credentialsProvider)
			.withRegion("ap-northeast-2")	/* check the region at AWS console */
			.build();
	}

	public static void main(String[] args) throws Exception {

		init();

		Scanner menu = new Scanner(System.in);
		Scanner id_string = new Scanner(System.in);
		int number = 0;

		while(true)
		{
			System.out.println("                                                            ");
			System.out.println("                                                            ");
			System.out.println("----------------------------------------------------------------");
			System.out.println("           Amazon AWS Control Panel using SDK               ");
			System.out.println("----------------------------------------------------------------");
			System.out.println("  1. list instance                2. available zones            ");
			System.out.println("  3. start instance               4. available regions          ");
			System.out.println("  5. stop instance                6. create instance            ");
			System.out.println("  7. reboot instance              8. list images                ");
			System.out.println("  9. check condor status         10. send command               ");
			System.out.println(" 11. list security group         12. create complete instance   ");
			System.out.println(" 13. create several instances    14. terminate instance         ");
			System.out.println(" 15. list instance with name                                    ");
			System.out.println("                                 99. quit                       ");
			System.out.println("----------------------------------------------------------------");

			System.out.print("Enter an integer: ");

			if(menu.hasNextInt()){
				number = menu.nextInt();
				}else {
					System.out.println("concentration!");
					break;
				}


			String instance_id = "";

			switch(number) {
			case 1:
				listInstances();
				break;

			case 2:
				availableZones();
				break;

			case 3:
				System.out.print("Enter instance id: ");
				if(id_string.hasNext())
					instance_id = id_string.nextLine();

				if(!instance_id.trim().isEmpty())
					startInstance(instance_id);
				break;

			case 4:
				availableRegions();
				break;

			case 5:
				System.out.print("Enter instance id: ");
				if(id_string.hasNext())
					instance_id = id_string.nextLine();

				if(!instance_id.trim().isEmpty())
					stopInstance(instance_id);
				break;

			case 6:
				System.out.print("Enter ami id: ");
				String ami_id = "";
				if(id_string.hasNext())
					ami_id = id_string.nextLine();

				if(!ami_id.trim().isEmpty())
					createInstance(ami_id);
				break;

			case 7:
				System.out.print("Enter instance id: ");
				if(id_string.hasNext())
					instance_id = id_string.nextLine();

				if(!instance_id.trim().isEmpty())
					rebootInstance(instance_id);
				break;

			case 8:
				listImages();
				break;

			case 9:
				System.out.print("Enter instance id: ");
				if(id_string.hasNext())
					instance_id = id_string.nextLine();

				if(!instance_id.trim().isEmpty())
					getCondorStatus(instance_id);
				break;

			case 10:
				System.out.print("Enter instance id: ");
				if(id_string.hasNext())
					instance_id = id_string.nextLine();

				if(!instance_id.trim().isEmpty()) {
					System.out.print("Enter command: ");
					String command = id_string.nextLine();

					if(!command.trim().isEmpty())
						sendCommand(instance_id, command);
				}
				break;

			case 11:
				listSecurityGroups();
				break;

			case 12:
				System.out.print("Enter ami id: ");
                String ami_id2 = "";
				if(id_string.hasNext())
					ami_id2 = id_string.nextLine();

				if(!ami_id2.trim().isEmpty()) {
					System.out.print("Enter instance name: ");
					String instance_name = id_string.nextLine();

					if(!instance_name.trim().isEmpty()) {
						System.out.print("Enter security group id: ");
						String securityGroup_id = id_string.nextLine();

						if(!securityGroup_id.trim().isEmpty())
							createCompleteInstance(ami_id2, instance_name, securityGroup_id);
					}
				}
				break;

			case 13:
				System.out.print("Enter ami id: ");
				String ami_id3 = "";
				if(id_string.hasNext())
					ami_id3 = id_string.nextLine();

				if(!ami_id3.trim().isEmpty()) {
					System.out.print("Enter instance name: ");
					String instance_name = id_string.nextLine();

					if(!instance_name.trim().isEmpty()) {
						System.out.print("Enter security group id: ");
						String securityGroup_id = id_string.nextLine();

						if(!securityGroup_id.trim().isEmpty()) {
							System.out.print("Enter instance number: ");
							Integer instance_number = Integer.valueOf(id_string.nextLine());
							createSeveralInstances(ami_id3, instance_name, securityGroup_id, instance_number);
						}
					}
				}
				break;

			case 14:
				System.out.print("Enter instance id: ");
				if(id_string.hasNext())
					instance_id = id_string.nextLine();

				if(!instance_id.trim().isEmpty())
					deleteInstance(instance_id);
				break;

			case 15:
				listInstancesWithName();
				break;

			case 99:
				System.out.println("bye!");
				menu.close();
				id_string.close();
				return;
			default: System.out.println("concentration!");
			}

		}

	}

	public static void listInstances() {

		System.out.println("Listing instances....");
		boolean done = false;

		DescribeInstancesRequest request = new DescribeInstancesRequest();

		while(!done) {
			DescribeInstancesResult response = ec2.describeInstances(request);

			for(Reservation reservation : response.getReservations()) {
				for(Instance instance : reservation.getInstances()) {
					System.out.printf(
						"[id] %s, " +
						"[AMI] %s, " +
						"[type] %s, " +
						"[state] %10s, " +
						"[monitoring state] %s",
						instance.getInstanceId(),
						instance.getImageId(),
						instance.getInstanceType(),
						instance.getState().getName(),
						instance.getMonitoring().getState());
				}
				System.out.println();
			}

			request.setNextToken(response.getNextToken());

			if(response.getNextToken() == null) {
				done = true;
			}
		}
	}

	public static void availableZones()	{

		System.out.println("Available zones....");
		try {
			DescribeAvailabilityZonesResult availabilityZonesResult = ec2.describeAvailabilityZones();
			Iterator <AvailabilityZone> iterator = availabilityZonesResult.getAvailabilityZones().iterator();

			AvailabilityZone zone;
			while(iterator.hasNext()) {
				zone = iterator.next();
				System.out.printf("[id] %s,  [region] %15s, [zone] %15s\n", zone.getZoneId(), zone.getRegionName(), zone.getZoneName());
			}
			System.out.println("You have access to " + availabilityZonesResult.getAvailabilityZones().size() +
					" Availability Zones.");

		} catch (AmazonServiceException ase) {
				System.out.println("Caught Exception: " + ase.getMessage());
				System.out.println("Reponse Status Code: " + ase.getStatusCode());
				System.out.println("Error Code: " + ase.getErrorCode());
				System.out.println("Request ID: " + ase.getRequestId());
		}

	}

	public static void startInstance(String instance_id)
	{

		System.out.printf("Starting .... %s\n", instance_id);
		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

		DryRunSupportedRequest<StartInstancesRequest> dry_request =
			() -> {
			StartInstancesRequest request = new StartInstancesRequest()
				.withInstanceIds(instance_id);

			return request.getDryRunRequest();
		};

		StartInstancesRequest request = new StartInstancesRequest()
			.withInstanceIds(instance_id);

		ec2.startInstances(request);

		System.out.printf("Successfully started instance %s", instance_id);
	}


	public static void availableRegions() {

		System.out.println("Available regions ....");

		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

		DescribeRegionsResult regions_response = ec2.describeRegions();

		for(Region region : regions_response.getRegions()) {
			System.out.printf(
				"[region] %15s, " +
				"[endpoint] %s\n",
				region.getRegionName(),
				region.getEndpoint());
		}
	}

	public static void stopInstance(String instance_id) {
		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

		DryRunSupportedRequest<StopInstancesRequest> dry_request =
			() -> {
			StopInstancesRequest request = new StopInstancesRequest()
				.withInstanceIds(instance_id);

			return request.getDryRunRequest();
		};

		try {
			StopInstancesRequest request = new StopInstancesRequest()
				.withInstanceIds(instance_id);

			ec2.stopInstances(request);
			System.out.printf("Successfully stop instance %s\n", instance_id);

		} catch(Exception e)
		{
			System.out.println("Exception: "+e.toString());
		}

	}

	public static void createInstance(String ami_id) {
		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

		RunInstancesRequest run_request = new RunInstancesRequest()
			.withImageId(ami_id)
			.withInstanceType(InstanceType.T2Micro)
			.withMaxCount(1)
			.withMinCount(1);

		RunInstancesResult run_response = ec2.runInstances(run_request);

		String reservation_id = run_response.getReservation().getInstances().get(0).getInstanceId();

		System.out.printf(
			"Successfully started EC2 instance %s based on AMI %s",
			reservation_id, ami_id);

	}

	public static void rebootInstance(String instance_id) {

		System.out.printf("Rebooting .... %s\n", instance_id);

		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

		try {
			RebootInstancesRequest request = new RebootInstancesRequest()
					.withInstanceIds(instance_id);

				RebootInstancesResult response = ec2.rebootInstances(request);

				System.out.printf(
						"Successfully rebooted instance %s", instance_id);

		} catch(Exception e)
		{
			System.out.println("Exception: "+e.toString());
		}


	}

	public static void listImages() {
		System.out.println("Listing images....");

		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

		DescribeImagesRequest request = new DescribeImagesRequest();
		ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();

		request.getFilters().add(new Filter().withName("owner-id").withValues("116981805751"));
		request.setRequestCredentialsProvider(credentialsProvider);

		DescribeImagesResult results = ec2.describeImages(request);

		for(Image images :results.getImages()){
			System.out.printf("[ImageID] %s, [Name] %s, [Owner] %s\n",
					images.getImageId(), images.getName(), images.getOwnerId());
		}

	}

	public static void getCondorStatus(String instance_id) {
		String user = "ec2-user";
		String keyPath = "jrcloud.pem";
		String command = "condor_status";

		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

		try {
			DescribeInstancesRequest request = new DescribeInstancesRequest().withInstanceIds(instance_id);
			DescribeInstancesResult response = ec2.describeInstances(request);

			String publicIp = "";
			for (Reservation reservation : response.getReservations()) {
				for (Instance instance : reservation.getInstances()) {
					publicIp = instance.getPublicIpAddress();
					if (!Objects.equals(publicIp, "")) break;
				}
			}

			if (publicIp == null) {
				System.err.println("Wrong instance ID: " + instance_id);
				return;
			}

			System.out.println("Instance Public IP: " + publicIp);

			JSch jsch = new JSch();
			jsch.addIdentity(keyPath);

			Session session = jsch.getSession(user, publicIp, 22);
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();

			System.out.println("VM 연결 성공: " + publicIp);

			ChannelExec channel = (ChannelExec) session.openChannel("exec");
			channel.setCommand(command);

			InputStream input = channel.getInputStream();

			channel.connect();

			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = input.read(buffer)) != -1) {
				System.out.print(new String(buffer, 0, bytesRead));
			}

			if (channel.isClosed()) {
				System.out.println("\nExit status: " + channel.getExitStatus());
			}

			channel.disconnect();
			session.disconnect();
			System.out.println("Disconnected from EC2 instance.");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void sendCommand(String instance_id, String command) {
		String user = "ec2-user";
		String keyPath = "jrcloud.pem";

		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

		try {
			DescribeInstancesRequest request = new DescribeInstancesRequest().withInstanceIds(instance_id);
			DescribeInstancesResult response = ec2.describeInstances(request);

			String publicIp = "";
			for (Reservation reservation : response.getReservations()) {
				for (Instance instance : reservation.getInstances()) {
					publicIp = instance.getPublicIpAddress();
					if (!Objects.equals(publicIp, "")) break;
				}
			}

			if (publicIp == null) {
				System.err.println("Wrong instance ID: " + instance_id);
				return;
			}

			System.out.println("Instance Public IP: " + publicIp);

			JSch jsch = new JSch();
			jsch.addIdentity(keyPath);

			Session session = jsch.getSession(user, publicIp, 22);
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();

			System.out.println("VM 연결 성공: " + publicIp);

			ChannelExec channel = (ChannelExec) session.openChannel("exec");
			channel.setCommand(command);

			InputStream input = channel.getInputStream();

			channel.connect();

			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = input.read(buffer)) != -1) {
				System.out.print(new String(buffer, 0, bytesRead));
			}

			if (channel.isClosed()) {
				System.out.println("\nExit status: " + channel.getExitStatus());
			}

			channel.disconnect();
			session.disconnect();
			System.out.println("Disconnected from EC2 instance.");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void listSecurityGroups() {

		System.out.println("Listing security groups....");
		boolean done = false;

		DescribeSecurityGroupsRequest request = new DescribeSecurityGroupsRequest();

		while(!done) {
			DescribeSecurityGroupsResult response = ec2.describeSecurityGroups(request);

			for(SecurityGroup securityGroup : response.getSecurityGroups()) {
				System.out.printf(
						"[id] %s, " +
								"[name] %s, " +
								"[description] %s, ",
						securityGroup.getGroupId(),
						securityGroup.getGroupName(),
						securityGroup.getDescription());
				System.out.println();
			}

			request.setNextToken(response.getNextToken());

			if(response.getNextToken() == null) {
				done = true;
			}
		}
	}

	public static void createCompleteInstance(String ami_id, String instance_name, String securityGroup_id) {
		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

		RunInstancesRequest run_request = new RunInstancesRequest()
				.withImageId(ami_id)
				.withInstanceType(InstanceType.T2Micro)
				.withKeyName("jrcloud")
				.withMaxCount(1)
				.withMinCount(1)
				.withSecurityGroupIds(securityGroup_id)
				.withTagSpecifications(Collections.singletonList(new TagSpecification()
						.withResourceType(ResourceType.Instance)
						.withTags(new Tag("Name", instance_name))));

		RunInstancesResult run_response = ec2.runInstances(run_request);

		String reservation_id = run_response.getReservation().getInstances().get(0).getInstanceId();

		System.out.printf(
				"Successfully started EC2 instance %s based on AMI %s",
				reservation_id, ami_id);
	}

	public static void createSeveralInstances(String ami_id, String instance_name, String securityGroup_id, Integer instance_number) {
		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

		for(int i = 0; i < instance_number; i++) {
			RunInstancesRequest run_request = new RunInstancesRequest()
					.withImageId(ami_id)
					.withInstanceType(InstanceType.T2Micro)
					.withKeyName("jrcloud")
					.withMaxCount(1)
					.withMinCount(1)
					.withSecurityGroupIds(securityGroup_id)
					.withTagSpecifications(Collections.singletonList(new TagSpecification()
							.withResourceType(ResourceType.Instance)
							.withTags(new Tag("Name", instance_name + i))));

			RunInstancesResult run_response = ec2.runInstances(run_request);

			String reservation_id = run_response.getReservation().getInstances().get(0).getInstanceId();

			System.out.printf(
					"Successfully started EC2 instance %s based on AMI %s",
					reservation_id, ami_id);
		}
	}

	public static void deleteInstance(String instance_id) {
		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

		try {
			TerminateInstancesRequest request = new TerminateInstancesRequest()
					.withInstanceIds(instance_id);

			TerminateInstancesResult result = ec2.terminateInstances(request);
			result.getTerminatingInstances().forEach(instance -> {
				System.out.printf(
						"Instance ID: %s is now in %s state%n",
						instance.getInstanceId(),
						instance.getCurrentState().getName()
				);
			});
		} catch(Exception e)
		{
			System.out.println("Exception: "+e.toString());
		}
	}

	public static void listInstancesWithName() {

		System.out.println("Listing instances....");
		boolean done = false;

		DescribeInstancesRequest request = new DescribeInstancesRequest();

		while(!done) {
			DescribeInstancesResult response = ec2.describeInstances(request);

			for(Reservation reservation : response.getReservations()) {
				for(Instance instance : reservation.getInstances()) {
					String instance_name = "";

					for(Tag tag : instance.getTags()) {
						if("Name".equals(tag.getKey())) {
							instance_name = tag.getValue();
						}
					}

					System.out.printf(
							"[name] %s, " +
							"[id] %s, " +
							"[AMI] %s, " +
							"[type] %s, " +
							"[state] %10s, ",
							instance_name,
							instance.getInstanceId(),
							instance.getImageId(),
							instance.getInstanceType(),
							instance.getState().getName());
				}
				System.out.println();
			}

			request.setNextToken(response.getNextToken());

			if(response.getNextToken() == null) {
				done = true;
			}
		}
	}
}
